package br.edu.infnet.micro.servicogerenciadoconsumidor.controller;

import br.edu.infnet.micro.servicogerenciadoconsumidor.model.Caminhao;
import br.edu.infnet.micro.servicogerenciadoconsumidor.service.GerenciadorConsumidoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caminhoes")
public class CaminhaoController {

    @Autowired
    private GerenciadorConsumidoresService gerenciadorService;

    @PostMapping
    public ResponseEntity<Caminhao> adicionarCaminhao(@RequestBody Caminhao caminhao) {
        Caminhao novoCaminhao = gerenciadorService.adicionarCaminhao(caminhao);
        return ResponseEntity.ok(novoCaminhao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Caminhao> atualizarCaminhao(@PathVariable String id, @RequestBody Caminhao caminhao) {
        return gerenciadorService.atualizarCaminhao(id, caminhao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCaminhao(@PathVariable String id) {
        if (gerenciadorService.excluirCaminhao(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Caminhao>> listarTodosCaminhoes() {
        List<Caminhao> caminhoes = gerenciadorService.listarTodosCaminhoes();
        return ResponseEntity.ok(caminhoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caminhao> buscarCaminhaoPorId(@PathVariable String id) {
        return gerenciadorService.buscarCaminhaoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
