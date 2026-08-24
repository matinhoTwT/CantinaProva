package com.senai.cantina.service;

import com.senai.cantina.dto.LancheRequestDTO;
import com.senai.cantina.dto.LancheResponseDTO;
import com.senai.cantina.dto.LancheResumoDTO;
import com.senai.cantina.exception.ResourceNotFoundException;
import com.senai.cantina.model.Lanche;
import com.senai.cantina.repository.LancheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LancheService {

    private final LancheRepository lancheRepository;

    public LancheResponseDTO cadastrar(LancheRequestDTO dto) {
        Lanche lanche = toEntity(dto);
        Lanche salvo = lancheRepository.save(lanche);
        return toResponseDTO(salvo);
    }

    public List<LancheResumoDTO> listarTodos() {
        return lancheRepository.findAll().stream()
                .map(lanche -> LancheResumoDTO.builder()
                        .nome(lanche.getNome())
                        .preco(lanche.getPreco())
                        .build())
                .toList();
    }

    public LancheResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    public LancheResponseDTO atualizar(Long id, LancheRequestDTO dto) {
        Lanche lanche = buscarEntidadePorId(id);

        lanche.setNome(dto.getNome());
        lanche.setDescricao(dto.getDescricao());
        lanche.setPreco(dto.getPreco());
        lanche.setCategoria(dto.getCategoria());
        lanche.setDisponivel(dto.getDisponivel());

        Lanche atualizado = lancheRepository.save(lanche);
        return toResponseDTO(atualizado);
    }

    public void remover(Long id) {
        Lanche lanche = buscarEntidadePorId(id);
        lancheRepository.delete(lanche);
    }

    private Lanche buscarEntidadePorId(Long id) {
        return lancheRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lanche não encontrado com o ID: " + id));
    }

    private Lanche toEntity(LancheRequestDTO dto) {
        return Lanche.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .categoria(dto.getCategoria())
                .disponivel(dto.getDisponivel())
                .build();
    }

    private LancheResponseDTO toResponseDTO(Lanche lanche) {
        return LancheResponseDTO.builder()
                .id(lanche.getId())
                .nome(lanche.getNome())
                .descricao(lanche.getDescricao())
                .preco(lanche.getPreco())
                .categoria(lanche.getCategoria())
                .disponivel(lanche.getDisponivel())
                .build();
    }

}
