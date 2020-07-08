package com.makovetskiy.jsvm.controller;

import com.makovetskiy.jsvm.dto.NewRequestResponseDto;
import com.makovetskiy.jsvm.dto.ScriptInfoDto;
import com.makovetskiy.jsvm.dto.ScriptStopDto;
import com.makovetskiy.jsvm.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController {

    @Autowired
    MainService mainService;

    @PostMapping
    @ResponseBody
    public NewRequestResponseDto sendRequest(HttpEntity<String> request) {
        return mainService.send(request.getBody());
    }

    @ResponseBody
    @GetMapping(value = "/script-status")
    public ScriptInfoDto getScriptStatus(@RequestParam(name = "id") String id) {
        return mainService.getScriptStatus(id);
    }

    @ResponseBody
    @PostMapping(value = "/script-stop")
    public ScriptStopDto stopScript(@RequestParam(name = "id") String id) {
        return mainService.stopScript(id);
    }

    @ResponseBody
    @GetMapping(value = "/script-list")
    public List<ScriptInfoDto> getAllScripts() {
        return mainService.getAll();
    }

    @GetMapping(value = "/script-delete")
    public void deleteScript(@RequestParam(name = "id") String id) {
        mainService.delete(id);
    }
}
