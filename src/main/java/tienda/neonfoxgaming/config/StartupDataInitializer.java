package tienda.neonfoxgaming.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import tienda.neonfoxgaming.model.Categoria;
import tienda.neonfoxgaming.model.Usuario;
import tienda.neonfoxgaming.repository.CategoriaRepository;
import tienda.neonfoxgaming.repository.ProductoRepository;
import tienda.neonfoxgaming.repository.UsuarioRepository;

@Component
public class StartupDataInitializer implements CommandLineRunner {
    private final ProductoRepository productos;
    private final CategoriaRepository categorias;
    private final UsuarioRepository usuarios;
    private final JsonMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public StartupDataInitializer(ProductoRepository productos, CategoriaRepository categorias,
            UsuarioRepository usuarios, JsonMapper objectMapper, PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate) {
        this.productos = productos;
        this.categorias = categorias;
        this.usuarios = usuarios;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        cargarCatalogoSiVacio();
        crearUsuarioDemoSiFalta("admin@neonfox.com", "admin123", "Administrador", "Admin");
        crearUsuarioDemoSiFalta("cliente@neonfox.com", "cliente123", "Cliente Demo", "Cliente");
    }

    private void cargarCatalogoSiVacio() throws Exception {
        if (productos.count() > 0) return;
        try (InputStream input = new ClassPathResource("seed/products.json").getInputStream()) {
            JsonNode catalogo = objectMapper.readTree(input);
            for (JsonNode node : catalogo) {
                String nombreCategoria = node.path("categoria").asText("Otros");
                Categoria categoria = categorias.findByNombreIgnoreCase(nombreCategoria)
                        .orElseGet(() -> categorias.save(new Categoria(nombreCategoria, "")));
                List<String> images = new ArrayList<>();
                node.path("images").forEach(image -> images.add(image.asText()));
                String image = node.path("image").asText("");
                jdbcTemplate.update("insert into productos (id, name, brand, description, price, image, stock, categoria_id) "
                                + "values (?, ?, ?, ?, ?, ?, ?, ?)",
                        node.path("id").asLong(), node.path("name").asText(), node.path("brand").asText(""),
                        node.path("description").asText(""), new BigDecimal(node.path("price").asText("0")),
                        image, node.path("stock").asInt(10), categoria.getId());
                List<String> imageRows = images.isEmpty() && !image.isBlank() ? List.of(image) : images;
                for (String imagePath : imageRows) {
                    jdbcTemplate.update("insert into producto_imagenes (producto_id, ruta) values (?, ?)",
                            node.path("id").asLong(), imagePath);
                }
            }
            try (var connection = jdbcTemplate.getDataSource().getConnection()) {
                String database = connection.getMetaData().getDatabaseProductName();
                if (database.equalsIgnoreCase("PostgreSQL")) {
                    jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('productos', 'id'), "
                            + "(select coalesce(max(id), 1) from productos))", Long.class);
                } else if (database.equalsIgnoreCase("H2")) {
                    Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from productos", Long.class);
                    jdbcTemplate.execute("alter table productos alter column id restart with " + nextId);
                }
            }
        }
    }

    private void crearUsuarioDemoSiFalta(String email, String password, String nombre, String role) {
        if (usuarios.existsByEmailIgnoreCase(email)) return;
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido("");
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRole(role);
        usuario.setEstado("Activo");
        usuarios.save(usuario);
    }
}
