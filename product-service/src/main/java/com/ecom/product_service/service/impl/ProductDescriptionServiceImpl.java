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

    private static final double AI_TEMPERATURE = 0.7;
    private static final int MAX_OUTPUT_TOKENS = 1000;
    private static final int ALTERNATIVE_MAX_TOKENS = 300;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String geminiModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductDescriptionResponse generateDescription(ProductDescriptionRequest request) {
        if (!isApiKeyConfigured()) {
            log.warn("Gemini API key chưa được cấu hình, sử dụng mô tả mẫu");
            return generateMockDescription(request);
        }

        try {
            String mainPrompt = buildMainDescriptionPrompt(request);
            String mainDescription = callGeminiApi(mainPrompt, MAX_OUTPUT_TOKENS);
            List<String> alternatives = generateAlternativeDescriptions(request);

            return ProductDescriptionResponse.builder()
                    .generatedDescription(mainDescription)
                    .alternativeDescriptions(alternatives)
                    .tone(request.getTone().toString())
                    .message("Đã tạo mô tả thành công bằng Gemini AI")
                    .build();

        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API, chuyển sang sử dụng mô tả mẫu: {}", e.getMessage());
            return generateMockDescription(request);
        }
    }

    private boolean isApiKeyConfigured() {
        return geminiApiKey != null && !geminiApiKey.trim().isEmpty();
    }

    private String buildMainDescriptionPrompt(ProductDescriptionRequest request) {
        String keywordsText = String.join(", ", request.getKeywords());
        String toneGuideline = getToneGuideline(request.getTone());

        StringBuilder prompt = new StringBuilder();
        prompt.append("BẠN LÀ CHUYÊN GIA VIẾT MÔ TẢ SẢN PHẨM. HÃY HOÀN THÀNH ĐẦY ĐỦ MÔ TẢ SAU:\n\n");
        
        prompt.append("Bạn là một copywriter chuyên nghiệp. Nhiệm vụ của bạn là viết mô tả sản phẩm hấp dẫn.\n\n");
        prompt.append("**Thông tin sản phẩm:**\n");
        prompt.append("- Từ khóa chính: ").append(keywordsText).append("\n");
        
        if (request.getAdditionalInfo() != null && !request.getAdditionalInfo().trim().isEmpty()) {
            prompt.append("- Thông tin bổ sung: ").append(request.getAdditionalInfo()).append("\n");
        }
        
        prompt.append("\n**Phong cách viết:**\n");
        prompt.append(toneGuideline).append("\n");
        
        prompt.append("\n**Yêu cầu:**\n");
        prompt.append("1. Độ dài: 100-150 từ (BẮT BUỘC VIẾT ĐỦ)\n");
        prompt.append("2. Ngôn ngữ: Tiếng Việt\n");
        prompt.append("3. Tập trung vào lợi ích cho khách hàng\n");
        prompt.append("4. Sử dụng các từ khóa một cách tự nhiên\n");
        prompt.append("5. Kết thúc bằng call-to-action mạnh mẽ\n");
        prompt.append("6. QUAN TRỌNG: Phải hoàn thành câu cuối cùng, KHÔNG được cắt đứt giữa chừng\n");
        prompt.append("\nViết ngay (CHỈ TRẢ VỀ MÔ TẢ, KHÔNG GHI CHÚ THÊM):\n");
        
        return prompt.toString();
    }

    private String getToneGuideline(ProductDescriptionRequest.DescriptionTone tone) {
        switch (tone) {
            case PROFESSIONAL:
                return "- Tone: Chuyên nghiệp, uy tín, đáng tin cậy\n" +
                       "- Ngôn từ: Trang trọng, chính xác, tập trung vào tính năng kỹ thuật\n" +
                       "- Tránh: Ngôn ngữ quá cảm tính, emoji, ký tự đặc biệt";
                
            case CASUAL:
                return "- Tone: Thân thiện, gần gũi, dễ hiểu\n" +
                       "- Ngôn từ: Đời thường, như nói chuyện với bạn bè\n" +
                       "- Có thể dùng: Emoji tinh tế, câu hỏi gợi mở";
                
            case MARKETING:
                return "- Tone: Năng động, hấp dẫn, thúc đẩy hành động\n" +
                       "- Ngôn từ: Sử dụng từ khóa bán hàng (HOT, SALE, ƯU ĐÃI)\n" +
                       "- Tạo cảm giác: Cấp bách, khan hiếm, giá trị vượt trội";
                
            default:
                return "- Tone: Cân bằng giữa chuyên nghiệp và thân thiện\n" +
                       "- Ngôn từ: Rõ ràng, dễ hiểu, thuyết phục";
        }
    }

    private List<String> generateAlternativeDescriptions(ProductDescriptionRequest request) {
        List<String> alternatives = new ArrayList<>();
        
        try {
            String alternativePrompt = buildAlternativePrompt(request);
            String altDescription = callGeminiApi(alternativePrompt, ALTERNATIVE_MAX_TOKENS);
            alternatives.add(altDescription);
        } catch (Exception e) {
            log.warn("Không thể tạo mô tả thay thế: {}", e.getMessage());
        }
        
        return alternatives;
    }
    private String buildAlternativePrompt(ProductDescriptionRequest request) {
        String keywordsText = String.join(", ", request.getKeywords());
        
        return String.format(
            "Viết một mô tả sản phẩm NGẮN GỌN và KHÁC BIỆT hoàn toàn so với mô tả trước.\n\n" +
            "Từ khóa: %s\n\n" +
            "Yêu cầu:\n" +
            "- Độ dài: 40-60 từ (VIẾT ĐỦ)\n" +
            "- Tập trung vào 1-2 điểm nổi bật nhất\n" +
            "- Phong cách: Ngắn gọn, súc tích, dễ nhớ\n" +
            "- Ngôn ngữ: Tiếng Việt\n" +
            "- QUAN TRỌNG: Hoàn thành câu cuối, không cắt đứt\n\n" +
            "Viết ngay (CHỈ TRẢ VỀ MÔ TẢ):",
            keywordsText
        );
    }

    private String callGeminiApi(String prompt, int maxTokens) {
        try {
            Map<String, Object> requestBody = buildGeminiRequestBody(prompt, maxTokens);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Build URL với API key
            String url = String.format("%s/%s:generateContent?key=%s", 
                                      geminiApiUrl, geminiModel, geminiApiKey);
            
            // Gọi API
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            return parseGeminiResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API: {}", e.getMessage());
            throw new BadRequestException("Không thể kết nối với AI: " + e.getMessage());
        }
    }


    private Map<String, Object> buildGeminiRequestBody(String prompt, int maxTokens) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Contents
        Map<String, String> part = new HashMap<>();
        part.put("text", prompt);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));
        
        requestBody.put("contents", List.of(content));
        
        // Generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", AI_TEMPERATURE);
        generationConfig.put("maxOutputTokens", maxTokens);
        requestBody.put("generationConfig", generationConfig);
        
        return requestBody;
    }

    private String parseGeminiResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("candidates")
                    .get(0)
                    .get("content")
                    .get("parts")
                    .get(0)
                    .get("text")
                    .asText()
                    .trim();
        } catch (Exception e) {
            log.error("Lỗi khi parse response từ Gemini: {}", e.getMessage());
            throw new BadRequestException("Response từ AI không hợp lệ");
        }
    }

    private ProductDescriptionResponse generateMockDescription(ProductDescriptionRequest request) {
        String keywordsText = String.join(", ", request.getKeywords());
        String mockDescription = buildMockDescriptionByTone(request.getTone(), keywordsText);
        
        // Thêm thông tin bổ sung nếu có
        if (request.getAdditionalInfo() != null && !request.getAdditionalInfo().trim().isEmpty()) {
            mockDescription += "\n\n" + request.getAdditionalInfo();
        }

        return ProductDescriptionResponse.builder()
                .generatedDescription(mockDescription)
                .alternativeDescriptions(new ArrayList<>())
                .tone(request.getTone().toString())
                .message("⚠️ Đang sử dụng mô tả mẫu (Gemini API chưa được cấu hình)")
                .build();
    }

    private String buildMockDescriptionByTone(ProductDescriptionRequest.DescriptionTone tone, 
                                             String keywordsText) {
        switch (tone) {
            case PROFESSIONAL:
                return String.format(
                    "Sản phẩm cao cấp tích hợp các tính năng: %s. " +
                    "Được nghiên cứu và phát triển theo tiêu chuẩn quốc tế, " +
                    "đảm bảo hiệu suất vượt trội và độ bền cao. " +
                    "Lựa chọn lý tưởng cho khách hàng có nhu cầu chất lượng cao.",
                    keywordsText
                );
                
            case CASUAL:
                return String.format(
                    "Bạn đang tìm kiếm sản phẩm với %s? " +
                    "Đây chính là điều bạn cần! " +
                    "Thiết kế thông minh, dễ sử dụng và giá cả hợp lý. " +
                    "Nhiều người dùng đã trải nghiệm và đánh giá tuyệt vời đấy!",
                    keywordsText
                );
                
            case MARKETING:
                return String.format(
                    "🔥 SIÊU SALE - ƯU ĐÃI KHỦNG! 🔥\n" +
                    "Sở hữu ngay sản phẩm HOT nhất với %s.\n" +
                    "⚡ Số lượng CÓ HẠN - Đặt hàng NGAY để không bỏ lỡ!\n" +
                    "✅ Cam kết chính hãng - Đổi trả miễn phí trong 30 ngày\n" +
                    "👉 Click MUA NGAY trước khi hết hàng!",
                    keywordsText
                );
                
            default:
                return String.format(
                    "Sản phẩm chất lượng cao với %s. " +
                    "Được đông đảo khách hàng tin dùng và đánh giá tích cực.",
                    keywordsText
                );
        }
    }
}
