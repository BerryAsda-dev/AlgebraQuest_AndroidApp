# QuizMateV1: Álgebra Quest

Juego educativo para aprender álgebra, construido como una **SPA (Single Page
Application)** con un motor de juego propio en JavaScript puro (sin
frameworks), más un **backend en PHP + MySQL** para guardar el progreso de
los jugadores en una base de datos real (funciona con XAMPP).

## Requisitos

- **XAMPP** (o cualquier stack con Apache + PHP + MySQL/MariaDB).
- No se necesita Node, npm, Composer ni nada más.

## Instalación con XAMPP (paso a paso)

**1. Copiar el proyecto**

Copia toda la carpeta `AlgebraQuest` dentro de `htdocs`:

- Windows: `C:\xampp\htdocs\AlgebraQuest`
- Mac: `/Applications/XAMPP/htdocs/AlgebraQuest`
- Linux: `/opt/lampp/htdocs/AlgebraQuest`

Después de clonar el repositorio, copia `api/config.example.php` como
`api/config.php` y edita las credenciales locales. `api/config.php` está
excluido de Git para no compartir contraseñas de la base de datos.

**2. Crear la base de datos**

1. Abre el **XAMPP Control Panel** y dale "Start" a **Apache** y a **MySQL**.
2. Abre `http://localhost/phpmyadmin` en tu navegador.
3. Ve a la pestaña **SQL**.
4. Abre el archivo `api/schema.sql` de este proyecto, copia todo su
   contenido, pégalo en el cuadro de phpMyAdmin y dale **"Continuar"**.

Esto crea la base `algebraquest` con las tablas de jugadores, progreso,
respuestas, administradores y trofeos.

Si ya tenías la base de datos creada, ejecuta `api/migracion_aula.sql`,
`api/migracion_administrador.sql` y `api/migracion_trofeos.sql` desde la
pestaña **SQL** de phpMyAdmin. Estas migraciones añaden las tablas sin borrar
el progreso existente.

**3. Revisar las credenciales (normalmente no hay que tocar nada)**

Abre `api/config.php`. Por defecto usa el usuario `root` sin contraseña, que
es la configuración estándar de XAMPP. Si tu MySQL tiene otra contraseña,
edítala ahí:

```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'algebraquest');
define('DB_USER', 'root');
define('DB_PASS', '');
```

**4. Abrir el juego**

```
http://localhost/AlgebraQuest/
```

Escribe un nombre y listo — tu progreso ya se guarda en MySQL.

## Jugar desde otro dispositivo (celular, otra PC...)

Como el progreso ahora vive en la base de datos (no en el navegador), puedes
escribir el mismo nombre desde cualquier dispositivo conectado a la misma
red que la PC con XAMPP, y vas a recuperar exactamente tu XP, monedas,
nivel y niveles completados.

1. Averigua la IP local de la PC con XAMPP (`ipconfig` en Windows,
   `ifconfig`/`ip addr` en Mac/Linux).
2. Desde el otro dispositivo, entra a `http://TU_IP/AlgebraQuest/`.
3. Escribe el mismo nombre de jugador que usaste antes.

Si no carga, revisa el firewall de tu PC (debe permitir conexiones
entrantes al puerto 80 / Apache).

## Arquitectura

```
index.html                   ← único punto de entrada (frontend)
css/                          ← estilos (tema, HUD, juego, mundos, ranking)
img/                          ← imágenes de personajes
js/
  data/
    worlds.js                 ← TODO el contenido: mundos y niveles
  core/
    player.js                  ← estado del jugador en memoria (XP, vidas...)
    storage.js                  ← cliente de la API (habla con api/*.php)
    engine.js                    ← motor: cargar nivel, comprobar respuesta
    hud.js                        ← barra superior (vidas, XP, monedas)
    router.js                      ← router basado en #/hash
  views/
    home.js                    ← login por nombre
    menu.js, worlds.js, levelSelect.js, levelIntro.js,
    game.js, achievements.js, ranking.js, settings.js
  app.js                     ← registra las rutas y arranca la app

api/                          ← backend PHP (habla con MySQL)
  config.php                   ← credenciales de conexión
  db.php                        ← conexión PDO + helpers compartidos
  schema.sql                     ← crea la base de datos y sus tablas
  login.php                       ← crea/recupera un jugador por nombre
  guardar.php                      ← guarda xp/monedas/vidas/nivel
  completar_nivel.php               ← marca un nivel como completado
  renombrar.php                      ← cambia el nombre de un jugador
  reset.php                           ← reinicia el progreso de un jugador
  ranking.php                          ← top 20 jugadores
  ranking_aula.php                     ← resultados agregados para el profesor
  registrar_respuesta.php              ← registra intentos y puntos de aula
  admin_login.php, admin_estado.php    ← sesión del administrador
  admin_logout.php, admin_auth.php    ← cierre y protección de rutas
  crear_administrador.php              ← alta inicial, solo desde CLI
  migracion_aula.sql                   ← crea la tabla de respuestas
  migracion_administrador.sql          ← crea las cuentas administrativas
```

## Cómo funciona el sistema de cuentas

Las cuentas de alumno siguen usando un nombre, sin contraseña:

- Si el nombre ya existe en la base de datos, se recupera su XP, monedas,
  nivel, vidas y niveles completados.
- Si no existe, se crea automáticamente con los valores iniciales.
- El dispositivo recuerda el último nombre usado (en `localStorage`, solo
  como comodidad) para no tener que escribirlo cada vez que abres el juego.
  Desde **Configuración → "Jugar con otro nombre"** puedes cerrar esa sesión
  local y entrar con otro nombre.

El administrador usa una cuenta separada, con contraseña, y no aparece como
jugador ni puede entrar al panel usando un nombre de alumno.

## Ranking

El menú tiene un botón **📊 Ranking** que muestra a los 20 mejores
jugadores (ordenados por nivel, luego XP, luego monedas), leyendo
directamente de la tabla `jugadores` en MySQL. Tu propia fila se resalta
si apareces en la lista.

El botón **Modo profesor** muestra un ranking de aula por puntos, aciertos,
El acceso docente está separado de la cuenta de jugador. Enciende Apache y
MySQL, importa `api/migracion_administrador.sql` si la base ya existía y crea
la cuenta desde la carpeta del proyecto con:

```powershell
C:\xampp\php\php.exe api\crear_administrador.php
```

El script solicita usuario y contraseña (mínimo 12 caracteres) en la consola;
las contraseñas se guardan con `password_hash`. Desde **Acceso docente** se
abre `/admin/login`; únicamente una sesión administrativa válida puede abrir
`/admin/ranking` o consultar `api/ranking_aula.php`. El panel muestra puntos,
aciertos, precisión y tiempo medio, se actualiza cada 10 segundos y permite
filtrar por mundo. El ranking reúne actividad histórica, no crea sesiones de
clase.

El menú de alumnos también incluye **Reto relámpago**, que abre al azar un
nivel disponible sin cambiar su cuenta ni sus datos.

## Cómo agregar un nivel nuevo

**No se crea ningún archivo HTML.** Solo edita `js/data/worlds.js` y agrega un
objeto dentro de `niveles` del mundo que corresponda:

```js
{
    id: 6,
    titulo: "Suma de Cubos",
    icono: "🧮",
    teoria: {
        definicion: "a³ + b³ = (a+b)(a² - ab + b²)",
        ejemplo: "x³ + 8 = (x+2)(x² - 2x + 4)"
    },
    preguntas: [
        {
            tipo: "opcion",           // o "arrastrar"
            enunciado: "x³ + 27",
            opciones: ["(x+3)(x²-3x+9)", "(x+3)³", "(x-3)(x²+3x+9)", "(x+9)(x²-3x+3)"],
            correcta: 0,
            xp: 30,
            monedas: 20
        }
    ]
}
```

Automáticamente aparecerá en la lista de niveles, quedará bloqueado hasta
completar el anterior, y funcionará con el mismo motor de preguntas (opción
múltiple o arrastrar y soltar). Las opciones se muestran en orden aleatorio
cada vez (no hace falta que "correcta" sea 0, aunque por convención así se
escribió el contenido incluido).

Para asignar un video a un tema, agrega `videoUrl` al objeto del nivel en
`js/data/worlds.js`, usando una URL de YouTube (`watch`, `youtu.be` o `embed`).
El nivel **Factor Común** ya incluye un video de ejemplo; reemplaza esa URL y
añade la misma propiedad a los demás niveles que tengan video.

## Cómo agregar un mundo nuevo

Agrega un objeto nuevo al arreglo `WORLDS` en `js/data/worlds.js`. Tiene dos
formas de desbloquearse:

- `desbloqueado: true` → siempre disponible desde el inicio (como Factorización).
- `requiere: "idDeOtroMundo"` → se desbloquea automáticamente al completar el
  100% de los niveles de ese otro mundo.

Con esto los 4 mundos actuales están encadenados: **Factorización →
Trigonometría → Ecuaciones Diferenciales → Álgebra Lineal**. Al terminar el
último nivel de un mundo, el juego muestra un aviso de "mundo desbloqueado" y
un botón directo para ir a jugarlo.

## Cómo agregar una pantalla nueva (poco común)

1. Crea `js/views/miVista.js` con un objeto `{ nombre, render(container, params) }`.
2. Inclúyelo en `index.html` junto a las demás vistas.
3. Registra la ruta en `js/app.js`:
   ```js
   Router.agregar("/mi-ruta/:param", MiVista);
   ```

## Solución de problemas

- **"No se pudo conectar con el servidor"** al escribir tu nombre: revisa que
  Apache y MySQL estén iniciados en el XAMPP Control Panel.
- **Error de conexión a la base de datos**: confirma que importaste
  `api/schema.sql` en phpMyAdmin, y que `api/config.php` tiene las
  credenciales correctas.
- **La página sale en blanco**: asegúrate de acceder por
  `http://localhost/AlgebraQuest/` (servido por Apache) y no abriendo
  `index.html` con doble clic — el backend PHP solo funciona a través del
  `index.html` con doble clic — el backend PHP solo funciona a través del
  servidor.
