package com.makovetskiy.jsvm.service;

import com.makovetskiy.jsvm.repository.JsEntityRepository;
import com.makovetskiy.jsvm.dto.NewRequestResponseDto;
import com.makovetskiy.jsvm.dto.ScriptInfoDto;
import com.makovetskiy.jsvm.dto.ScriptStopDto;
import com.makovetskiy.jsvm.model.JsEntity;
import com.makovetskiy.jsvm.model.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MainServiceImpl implements MainService {

    private static final String JS_REQUEST_NOT_FOUND = "script with id %s not found";

    private static List<JsCodeExecutor> threads = new ArrayList<>();

    @Autowired
    JsEntityRepository jsEntityRepository;

    @Override
    public NewRequestResponseDto send(String request) {

        JsEntity jsEntity = JsEntity.builder()
                .request(request)
                .status(Status.EXECUTE)
                .isDeleted(false)
                .build();
        jsEntity = jsEntityRepository.save(jsEntity);
        JsCodeExecutor jsCodeExecutor = new JsCodeExecutor(jsEntity, jsEntityRepository);
        jsCodeExecutor.setName(jsEntity.getId());
        jsCodeExecutor.start();
        threads.add(jsCodeExecutor);
        return NewRequestResponseDto.builder().id(jsEntity.getId()).build();
    }

    @Override
    public ScriptInfoDto getScriptStatus(String id) {
        JsEntity jsEntity = jsEntityRepository
                .findById(id)
                .filter(jsEntiti -> !jsEntiti.getIsDeleted())
                .orElseThrow(() -> new RuntimeException(String.format(JS_REQUEST_NOT_FOUND, id)));

        String response;
        String error;

        if (jsEntity.getStatus().equals(Status.EXECUTE)) {
            JsCodeExecutor jsCodeExecutor = threads.stream()
                    .filter(executor -> StringUtils.equals(executor.getName(), id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format(JS_REQUEST_NOT_FOUND, id)));
            jsEntity = jsCodeExecutor.getJsEntity();
            response = jsCodeExecutor.getResponse().toString();
            error = jsCodeExecutor.getError().toString();

        } else {
            response = jsEntity.getConsoleResponse();
            error = jsEntity.getConsoleError();
        }

        return ScriptInfoDto.builder()
                .id(jsEntity.getId())
                .request(jsEntity.getRequest())
                .consoleResponse(response)
                .consoleError(error)
                .status(jsEntity.getStatus())
                .build();
    }

    @Override
    public ScriptStopDto stopScript(String id) {
        JsCodeExecutor jsCodeExecutor = threads.stream()
                .filter(executor -> StringUtils.equals(executor.getName(), id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format(JS_REQUEST_NOT_FOUND, id)));

        jsCodeExecutor.interrupt();
        return ScriptStopDto.builder().id(id).isStopped(true).build();
    }

    @Override
    public List<ScriptInfoDto> getAll() {
        return jsEntityRepository.findAll().stream()
                .filter(jsEntity -> !jsEntity.getIsDeleted())
                .map(jsEntity -> ScriptInfoDto.builder()
                        .id(jsEntity.getId())
                        .request(jsEntity.getRequest())
                        .consoleResponse(jsEntity.getConsoleResponse())
                        .consoleError(jsEntity.getConsoleError())
                        .status(jsEntity.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        JsEntity jsEntity = jsEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(JS_REQUEST_NOT_FOUND, id)));

        jsEntity.setIsDeleted(true);
        jsEntityRepository.save(jsEntity);
    }
}
