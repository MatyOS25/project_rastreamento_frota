package br.edu.infnet.micro.consumidor_caminhao.repository;

import br.edu.infnet.micro.consumidor_caminhao.model.LocalizacaoCassandra;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizacaoCassandraRepository extends CassandraRepository<LocalizacaoCassandra, String> {
}
