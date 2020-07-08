package com.makovetskiy.jsvm.service;

import com.makovetskiy.jsvm.repository.JsEntityRepository;
import com.makovetskiy.jsvm.model.JsEntity;
import com.makovetskiy.jsvm.model.Status;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

@Slf4j
public class JsCodeExecutor extends Thread {

    @Getter
    private JsEntity jsEntity;

    @Getter
    private StringBuilder response = new StringBuilder();

    @Getter
    private StringBuilder error = new StringBuilder();

    private JsEntityRepository jsEntityRepository;

    public JsCodeExecutor(JsEntity jsEntity, JsEntityRepository jsEntityRepository) {
        this.jsEntity = jsEntity;
        this.jsEntityRepository = jsEntityRepository;
    }

    @Override
    public void run() {
        String request = jsEntity.getRequest();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"js", "-e", request});
            BufferedWriter r = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            r.write(request);
            r.newLine();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = responseReader.readLine()) != null) {
                if (this.isInterrupted()) {
                    process.destroy();
                    jsEntity.setStatus(Status.STOP);
                }
                response.append(line);
                response.append("\n");
                log.info(this.getName() + ": " + line);
            }
            while ((line = errorReader.readLine()) != null) {
                error.append(line);
                error.append("\n");
                log.info(this.getName() + ": " + line);
            }

            jsEntity.setConsoleResponse(response.toString());
            jsEntity.setConsoleError(error.toString());

            if (jsEntity.getStatus() != null && !jsEntity.getStatus().equals(Status.STOP)) {
                if (!StringUtils.isEmpty(jsEntity.getConsoleError())) {
                    jsEntity.setStatus(Status.ERROR);
                } else {
                    jsEntity.setStatus(Status.SUCCESS);
                }
            }

            jsEntityRepository.save(jsEntity);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
