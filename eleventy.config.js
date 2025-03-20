import EleventyImage from "@11ty/eleventy-img";
import path from "path";

export default function(eleventyConfig) {
  // Agregar soporte para imágenes optimizadas
  eleventyConfig.addShortcode("image", async (src, alt, className = "", sizes = "100vw") => {
    let metadata = await EleventyImage(path.join("site", src), {
      widths: [300, 600, "auto"], // Diferentes tamaños para responsive
      formats: ["avif", "webp", "jpeg"], // Formatos optimizados
      outputDir: "./_site/img/", // Carpeta de salida
      urlPath: "/img/", // Ruta de acceso en la web
    });

    let imageAttributes = {
      alt,
      sizes,
      loading: "lazy",
      decoding: "async",
      class: className,
    };

    return EleventyImage.generateHTML(metadata, imageAttributes);
  });

  // Copiar archivos estáticos (Ejemplo: CSS, JS, imágenes sin procesar)
  eleventyConfig.addPassthroughCopy({ "site/assets/": "assets" });

  return {
    dir: {
      input: "site/content",  // Entrada de los archivos de contenido
      includes: "includes",   // Incluir plantillas parciales
      output: "_site"         // Carpeta de salida
    }
  };
}
