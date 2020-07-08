package com.makovetskiy.jsvm.service;

import com.makovetskiy.jsvm.dto.NewRequestResponseDto;
import com.makovetskiy.jsvm.dto.ScriptInfoDto;
import com.makovetskiy.jsvm.dto.ScriptStopDto;

import java.util.List;

public interface MainService {
    NewRequestResponseDto send(String request);

    ScriptInfoDto getScriptStatus(String id);

    ScriptStopDto stopScript(String id);

    List<ScriptInfoDto> getAll();

    void delete(String id);
}
