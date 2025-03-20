import EleventyImage from "@11ty/eleventy-img";
import path from "path";

export default function(eleventyConfig) {
  // Configurar Nunjucks para poder usar `{% image ... %}`
  eleventyConfig.setLibrary("njk", require("nunjucks"));
  eleventyConfig.setLibrary("liquid", require("liquidjs"));

  // Definir un shortcode global para imágenes
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

  // Asegurar que copia imágenes estáticas correctamente
  eleventyConfig.addPassthroughCopy("site/content");

  return {
    dir: {
      input: "site/content",
      output: "_site",
    }
  };
}