import pluginWebc from "@11ty/eleventy-plugin-webc";
import EleventyImage from "@11ty/eleventy-img";
import path from "path";

export default function(eleventyConfig) {
  // Habilitar WebC para componentes y layouts
  eleventyConfig.addPlugin(pluginWebc, {
    components: [
      "./site/_includes/components/**/*.webc",
    ]
  });

  // Configurar Nunjucks y Liquid
  eleventyConfig.setLibrary("njk", require("nunjucks"));
  eleventyConfig.setLibrary("liquid", require("liquidjs"));

  // Shortcode para imágenes optimizadas
  eleventyConfig.addShortcode("image", async (src, alt, className = "img-fluid", width = "100%") => {
    let metadata = await EleventyImage(src, {
      widths: [300, 600, "auto"],
      formats: ["avif", "webp", "jpeg"],
      outputDir: "./_site/img/",
      urlPath: "/img/",
    });

    let imageAttributes = {
      alt,
      class: className,
      loading: "lazy",
      decoding: "async",
      width,
    };

    return EleventyImage.generateHTML(metadata, imageAttributes);
  });

  // Copiar archivos estáticos (imágenes y descargas)
  eleventyConfig.addPassthroughCopy({ "site/assets/images": "img" });
  eleventyConfig.addPassthroughCopy({ "site/downloads": "downloads" });

  return {
    dir: {
      input: "site/content",         // Carpeta donde están las páginas
      includes: "../site/_includes", // Donde están los layouts y componentes
      layouts: "../site/_includes/layouts", // Donde están los layouts
      output: "_site",               // Carpeta de salida
    }
  };
}
