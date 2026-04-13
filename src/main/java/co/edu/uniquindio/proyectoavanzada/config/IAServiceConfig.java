package co.edu.uniquindio.proyectoavanzada.config;

import co.edu.uniquindio.proyectoavanzada.repositories.SolicitudRepository;
import co.edu.uniquindio.proyectoavanzada.services.IAService;
import co.edu.uniquindio.proyectoavanzada.services.impl.IAServiceImpl;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class IAServiceConfig{

    /*
    @Bean
    public IAService iaService(SolicitudRepository tareaRepository,
                               ChatClient.Builder chatClientBuilder) {

        try {
            ChatClient chatClient = chatClientBuilder.build();
            return new IAServiceImpl(chatClient, tareaRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/
}
