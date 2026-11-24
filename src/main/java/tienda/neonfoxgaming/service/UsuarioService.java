package tienda.neonfoxgaming.service;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tienda.neonfoxgaming.dto.LoginRequest;
import tienda.neonfoxgaming.dto.RegistroRequest;
import tienda.neonfoxgaming.dto.UsuarioDto;
import tienda.neonfoxgaming.dto.UsuarioRequest;
import tienda.neonfoxgaming.model.Usuario;
import tienda.neonfoxgaming.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDto> todos() {
        return usuarios.findAll().stream().map(DtoMapper::usuario).toList();
    }

    public UsuarioDto porId(Long id) {
        return DtoMapper.usuario(usuarios.findById(id).orElseThrow(() -> noExiste(id)));
    }

    public UsuarioDto porEmail(String email) {
        return DtoMapper.usuario(usuarios.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario.")));
    }

    @Transactional
    public UsuarioDto registrar(RegistroRequest request) {
        if (!rutChilenoValido(request.rut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUT no es válido.");
        }
        if (usuarios.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo.");
        }
        if (request.rut() != null && usuarios.existsByRutIgnoreCase(request.rut().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese RUT.");
        }
        Usuario u = new Usuario();
        u.setNombre(request.nombre().trim());
        u.setApellido(request.apellido().trim());
        u.setRut(request.rut().trim());
        u.setEmail(normalizarEmail(request.email()));
        u.setPasswordHash(passwordEncoder.encode(request.password()));
        u.setTelefono(valor(request.telefono()));
        u.setRegion(valor(request.region()));
        u.setComuna(valor(request.comuna()));
        u.setRole("Cliente");
        u.setEstado("Activo");
        return DtoMapper.usuario(usuarios.save(u));
    }

    public UsuarioDto autenticar(LoginRequest request) {
        Usuario u = usuarios.findByEmailIgnoreCase(normalizarEmail(request.email()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos."));
        if (!passwordEncoder.matches(request.password(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos.");
        }
        if ("Inactivo".equalsIgnoreCase(u.getEstado())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta cuenta está desactivada.");
        }
        return DtoMapper.usuario(u);
    }

    @Transactional
    public UsuarioDto crear(UsuarioRequest request) {
        if (usuarios.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo.");
        }
        if (request.rut() != null && !request.rut().isBlank() && usuarios.existsByRutIgnoreCase(request.rut().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese RUT.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria.");
        }
        Usuario u = new Usuario();
        aplicar(u, request);
        u.setPasswordHash(passwordEncoder.encode(request.password()));
        return DtoMapper.usuario(usuarios.save(u));
    }

    @Transactional
    public UsuarioDto actualizar(Long id, UsuarioRequest request) {
        Usuario u = usuarios.findById(id).orElseThrow(() -> noExiste(id));
        String normalizedEmail = normalizarEmail(request.email());
        usuarios.findByEmailIgnoreCase(normalizedEmail).filter(found -> !found.getId().equals(id)).ifPresent(found -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo.");
        });
        if (request.rut() != null && !request.rut().isBlank() && usuarios.existsByRutIgnoreCase(request.rut().trim())
                && !request.rut().equalsIgnoreCase(u.getRut())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese RUT.");
        }
        aplicar(u, request);
        if (request.password() != null && !request.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return DtoMapper.usuario(usuarios.save(u));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarios.existsById(id)) throw noExiste(id);
        usuarios.deleteById(id);
    }

    private void aplicar(Usuario u, UsuarioRequest request) {
        u.setNombre(request.nombre().trim());
        u.setApellido(valor(request.apellido()));
        u.setRut(valor(request.rut()));
        u.setEmail(normalizarEmail(request.email()));
        u.setTelefono(valor(request.telefono()));
        u.setRegion(valor(request.region()));
        u.setComuna(valor(request.comuna()));
        u.setRole("Admin".equalsIgnoreCase(request.role()) ? "Admin" : "Cliente");
        u.setEstado("Inactivo".equalsIgnoreCase(request.estado()) ? "Inactivo" : "Activo");
    }

    private String normalizarEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
    private String valor(String value) { return value == null ? "" : value.trim(); }
    private boolean rutChilenoValido(String rut) {
        if (rut == null) return false;
        String limpio = rut.replaceAll("[^0-9kK]", "").toUpperCase(Locale.ROOT);
        if (limpio.length() < 8) return false;
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        if (!cuerpo.chars().allMatch(Character::isDigit)) return false;
        int suma = 0;
        int factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.digit(cuerpo.charAt(i), 10) * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int digito = 11 - suma % 11;
        String esperado = digito == 11 ? "0" : digito == 10 ? "K" : String.valueOf(digito);
        return esperado.equals(limpio.substring(limpio.length() - 1));
    }
    private ResponseStatusException noExiste(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + id + ".");
    }
}
