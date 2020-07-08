package com.makovetskiy.jsvm.service

import com.makovetskiy.jsvm.dto.ScriptStopDto
import com.makovetskiy.jsvm.model.JsEntity
import com.makovetskiy.jsvm.model.Status
import com.makovetskiy.jsvm.repository.JsEntityRepository
import spock.lang.Specification

class MainServiceTest extends Specification {

    def "save and start execute js code"() {
        given:
        def request = "request"
        def jsEntity = new JsEntity()
        jsEntity.setRequest(request)
        jsEntity.setIsDeleted(false)
        jsEntity.setStatus(Status.EXECUTE)

        JsEntityRepository jsEntityRepository = Stub() {
            save(_ as JsEntity) >> {
                jsEntity.setId("c40f20e0-a1fa-4e5b-a978-21dfb167daf9")
                return jsEntity
            }
        }

        MainService mainService = new MainServiceImpl(jsEntityRepository: jsEntityRepository)
        when:
        def newRequestResponseDto = mainService.send(request)

        then:
        "c40f20e0-a1fa-4e5b-a978-21dfb167daf9" == newRequestResponseDto.getId()
    }

    def "get status of success js code"() {
        given:
        def jsEntity = new JsEntity()
        jsEntity.setId("c40f20e0-a1fa-4e5b-a978-21dfb167daf9")
        jsEntity.setRequest("request")
        jsEntity.setConsoleResponse("response")
        jsEntity.setConsoleError("error")
        jsEntity.setStatus(Status.SUCCESS)
        jsEntity.setIsDeleted(false)

        JsEntityRepository jsEntityRepository = Stub() {
            findById(_ as String) >> Optional.of(jsEntity)
        }

        MainService mainService = new MainServiceImpl(jsEntityRepository: jsEntityRepository)
        when:
        def scriptInfoDto = mainService.getScriptStatus("c40f20e0-a1fa-4e5b-a978-21dfb167daf9")

        then:
        "c40f20e0-a1fa-4e5b-a978-21dfb167daf9" == scriptInfoDto.getId()
        "request" == scriptInfoDto.getRequest()
        "response" == scriptInfoDto.getConsoleResponse()
        "error" == scriptInfoDto.getConsoleError()
        Status.SUCCESS.equals(scriptInfoDto.getStatus())
    }

    def "get status of executable js code"() {
        def jsEntity = new JsEntity()
        jsEntity.setId("c40f20e0-a1fa-4e5b-a978-21dfb167daf8")
        jsEntity.setRequest("while(true){console.log('bye')}")
        jsEntity.setStatus(Status.EXECUTE)
        jsEntity.setIsDeleted(false)

        JsEntityRepository jsEntityRepository = Stub() {
            findById(_ as String) >> Optional.of(jsEntity)
        }

        def jsCodeExecutor = new JsCodeExecutor(jsEntity, jsEntityRepository)
        jsCodeExecutor.setName("c40f20e0-a1fa-4e5b-a978-21dfb167daf8")
        jsCodeExecutor.response = new StringBuilder("response")
        jsCodeExecutor.error = new StringBuilder("error")
        MainService mainService = new MainServiceImpl(jsEntityRepository: jsEntityRepository)
        mainService.threads.add(jsCodeExecutor)

        when:
        def scriptInfoDto = mainService.getScriptStatus("c40f20e0-a1fa-4e5b-a978-21dfb167daf8")

        then:
        "c40f20e0-a1fa-4e5b-a978-21dfb167daf8" == scriptInfoDto.getId()
        "while(true){console.log('bye')}" == scriptInfoDto.getRequest()
        "response" == scriptInfoDto.getConsoleResponse()
        "error" == scriptInfoDto.getConsoleError()
        Status.EXECUTE.equals(scriptInfoDto.getStatus())
    }

    def "stop executable js code"() {
        given:
        def request = "request"
        def jsEntity = new JsEntity()
        jsEntity.setRequest(request)
        jsEntity.setIsDeleted(false)
        jsEntity.setStatus(Status.EXECUTE)
        JsEntityRepository jsEntityRepository = Stub()
        def jsCodeExecutor = new JsCodeExecutor(jsEntity, jsEntityRepository)
        jsCodeExecutor.setName("c40f20e0-a1fa-4e5b-a978-21dfb167daf9")

        def mainService = new MainServiceImpl(jsEntityRepository: jsEntityRepository)
        mainService.threads.add(jsCodeExecutor)
        when:
        ScriptStopDto scriptStopDto = mainService.stopScript("c40f20e0-a1fa-4e5b-a978-21dfb167daf9")

        then:
        "c40f20e0-a1fa-4e5b-a978-21dfb167daf9" == scriptStopDto.getId()
        scriptStopDto.getIsStopped()
    }
}