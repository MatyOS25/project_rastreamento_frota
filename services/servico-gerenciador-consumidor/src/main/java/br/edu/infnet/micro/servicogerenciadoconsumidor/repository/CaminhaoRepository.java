package br.edu.infnet.micro.servicogerenciadoconsumidor.repository;

import br.edu.infnet.micro.servicogerenciadoconsumidor.model.Caminhao;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CaminhaoRepository extends MongoRepository<Caminhao, String> {
    List<Caminhao> findByAtivo(boolean ativo);
}
