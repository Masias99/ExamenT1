package com.cibertec.tiendavirtual.controller;

import com.cibertec.tiendavirtual.dto.ClienteDTO;
import com.cibertec.tiendavirtual.exception.ResourceNotFoundException;
import com.cibertec.tiendavirtual.model.Cliente;
import com.cibertec.tiendavirtual.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listar() {
        return ResponseEntity.ok(
                clienteRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
        return ResponseEntity.ok(toDTO(cliente));
    }

    @GetMapping("/buscar-por-email")
    public ResponseEntity<ClienteDTO> buscarPorEmail(@RequestParam String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con email " + email));
        return ResponseEntity.ok(toDTO(cliente));
    }


    @GetMapping("/con-pedidos")
    public ResponseEntity<List<ClienteDTO>> buscarClientesConPedidos() {
        return ResponseEntity.ok(
                clienteRepository.buscarClientesConPedidos().stream().map(this::toDTO).collect(Collectors.toList())
        );
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteDTO>> buscarPorNombreOApellido(@RequestParam String texto) {
        return ResponseEntity.ok(
                clienteRepository.buscarPorNombreOApellido(texto).stream().map(this::toDTO).collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO dto) {
        Cliente cliente = Cliente.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();
        Cliente guardado = clienteRepository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @RequestBody ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
        cliente.setNombres(dto.getNombres());
        cliente.setApellidos(dto.getApellidos());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        return ResponseEntity.ok(toDTO(clienteRepository.save(cliente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con id " + id);
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ClienteDTO toDTO(Cliente c) {
        return ClienteDTO.builder()
                .id(c.getId())
                .nombres(c.getNombres())
                .apellidos(c.getApellidos())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .direccion(c.getDireccion())
                .build();
    }
}
