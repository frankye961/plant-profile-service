package com.smart.watering.system.be.ai.engine;

import com.smart.watering.model.Zone;
import com.smart.watering.system.be.ai.model.records.ZoneSuggestion;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AiEngine {

    @Value("${openai.model}")
    private static String aiModel;
    private final ChatClient client;

    public ZoneSuggestion retrieveZoneSuggestion(Zone zone) {
        var converter = getConverter();
        return client.prompt()
                .system(generatePromptForSystem())
                .user(generatePromptForUser(zone, converter))
                .call()
                .entity(converter);
    }

    private String generatePromptForSystem() {
        return new StringBuilder()
                .append("You generate conservative irrigation configuration.")
                .append("Return ONLY JSON that matches the schema.")
                .append("If uncertain, lower confidence and be conservative.\n")
                .append("Ensure: critical < low < targetMin < targetMax, all within 0..100.\n")
                .toString();
    }

    private String generatePromptForUser(Zone zone, BeanOutputConverter<?> converter) {
        return """
                Generate thresholds, constraints, and calibrationPolicy for this zone.
                Zone context:
                %s
                
                Output format:
                %s
                """.formatted(zone, converter.getFormat());
    }

    private BeanOutputConverter<ZoneSuggestion> getConverter() {
        return new BeanOutputConverter<>(ZoneSuggestion.class);
    }
}
