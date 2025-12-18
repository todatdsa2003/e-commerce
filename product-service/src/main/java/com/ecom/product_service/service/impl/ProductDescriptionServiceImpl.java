package com.ecom.product_service.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecom.product_service.dto.ProductDescriptionRequest;
import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.response.ProductDescriptionResponse;
import com.ecom.product_service.service.ProductDescriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductDescriptionServiceImpl implements ProductDescriptionService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductDescriptionResponse generateDescription(ProductDescriptionRequest request) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            return generateMockDescription(request);
        }

        try {
            String prompt = buildPrompt(request);
            String generatedText = callGemini(prompt);

            return ProductDescriptionResponse.builder()
                    .generatedDescription(generatedText)
                    .alternativeDescriptions(generateAlternatives(request))
                    .tone(request.getTone().toString())
                    .message("Đã tạo mô tả thành công bằng Gemini AI")
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API: ", e);
            return generateMockDescription(request);
        }
    }

    private String buildPrompt(ProductDescriptionRequest request) {
        String keywordsText = String.join(", ", request.getKeywords());
        String toneInstruction = getToneInstruction(request.getTone());

        StringBuilder prompt = new StringBuilder();
        prompt.append("Viết mô tả sản phẩm bằng tiếng Việt với các từ khóa sau: ")
              .append(keywordsText)
              .append(". ");

        if (request.getAdditionalInfo() != null && !request.getAdditionalInfo().isEmpty()) {
            prompt.append("Thông tin thêm: ").append(request.getAdditionalInfo()).append(". ");
        }

        prompt.append(toneInstruction);
        prompt.append(" Mô tả nên dài khoảng 100-150 từ, hấp dẫn và dễ hiểu.");

        return prompt.toString();
    }

    private String getToneInstruction(ProductDescriptionRequest.DescriptionTone tone) {
        switch (tone) {
            case PROFESSIONAL:
                return "Giọng văn chuyên nghiệp, trang trọng, tập trung vào tính năng và chất lượng.";
            case CASUAL:
                return "Giọng văn thân thiện, gần gũi, dễ hiểu như đang nói chuyện với bạn bè.";
            case MARKETING:
                return "Giọng văn bán hàng, thu hút, tạo cảm xúc muốn mua ngay.";
            default:
                return "Giọng văn cân bằng, vừa chuyên nghiệp vừa thân thiện.";
        }
    }

    private String callGemini(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            parts.add(part);
            
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);
            
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 500);
            requestBody.put("generationConfig", generationConfig);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = geminiApiUrl + "/" + geminiModel + ":generateContent?key=" + geminiApiKey;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText()
                    .trim();

        } catch (Exception e) {
            log.error("Lỗi khi parse response từ Gemini: ", e);
            throw new BadRequestException("Không thể tạo mô tả từ AI: " + e.getMessage());
        }
    }

    private List<String> generateAlternatives(ProductDescriptionRequest request) {
        List<String> alternatives = new ArrayList<>();
        return alternatives;
    }

    private ProductDescriptionResponse generateMockDescription(ProductDescriptionRequest request) {
        String keywordsText = String.join(", ", request.getKeywords());
        
        String mockDescription;
        switch (request.getTone()) {
            case PROFESSIONAL:
                mockDescription = String.format(
                    "Sản phẩm cao cấp với các đặc điểm nổi bật: %s. " +
                    "Được thiết kế và sản xuất theo tiêu chuẩn quốc tế, " +
                    "đảm bảo chất lượng và độ bền cao. " +
                    "Phù hợp cho những khách hàng đòi hỏi cao về chất lượng và tính năng.",
                    keywordsText
                );
                break;
            case CASUAL:
                mockDescription = String.format(
                    "Bạn đang tìm một sản phẩm với %s? " +
                    "Đây chính là lựa chọn hoàn hảo dành cho bạn! " +
                    "Dễ sử dụng, tiện lợi và đáng đồng tiền bát gạo. " +
                    "Nhiều bạn đã dùng và đánh giá rất tốt đấy!",
                    keywordsText
                );
                break;
            case MARKETING:
                mockDescription = String.format(
                    "SALE SỐC - KHUYẾN MÃI CỰC ĐẠI!\n" +
                    "Sở hữu ngay sản phẩm HOT với %s. " +
                    "Số lượng có hạn - Đặt hàng ngay hôm nay để nhận ưu đãi đặc biệt! " +
                    "Cam kết chất lượng - Đổi trả trong 30 ngày. " +
                    "Nhanh tay kẻo hết!",
                    keywordsText
                );
                break;
            default:
                mockDescription = String.format(
                    "Sản phẩm chất lượng với %s. " +
                    "Được nhiều khách hàng tin dùng và đánh giá cao.",
                    keywordsText
                );
        }

        if (request.getAdditionalInfo() != null && !request.getAdditionalInfo().isEmpty()) {
            mockDescription += " " + request.getAdditionalInfo();
        }

        return ProductDescriptionResponse.builder()
                .generatedDescription(mockDescription)
                .alternativeDescriptions(new ArrayList<>())
                .tone(request.getTone().toString())
                .message("Lỗi kết nối AI, sử dụng mô tả mẫu")
                .build();
    }
}
