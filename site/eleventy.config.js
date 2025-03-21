import pluginWebc from "@11ty/eleventy-plugin-webc";

/** @param {import('@11ty/eleventy').UserConfig} eleventyConfig */
export default function (eleventyConfig) {
    eleventyConfig.ignores.add("README.md");

    eleventyConfig.addPlugin(pluginWebc, {
        components: [
            "./_includes/components/**/*.webc",
        ]
    });

    eleventyConfig.setServerOptions({ domDiff: false });

    return {
        dir: {
            input: "content",
            includes: "../_includes",
            data: "../_data",
        }
    };
}