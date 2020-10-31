package org.panaggelica.xls_json_sieve;

import lombok.SneakyThrows;
import org.panaggelica.xls_json_sieve.model.ODHModel;
import org.panaggelica.xls_json_sieve.util.IOUtil;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Component
public class ODHLoader {

    @SneakyThrows
    @PostConstruct
    private void loadODH() {
        final InputStream odh = Thread.currentThread().getContextClassLoader().getResourceAsStream("odh20200928");
        IOUtil.objectMapper.readValue(odh, ODHModel.class);

    }
}
