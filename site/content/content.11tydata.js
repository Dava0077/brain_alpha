export default {
	layout: "base.webc",
	image: (src, alt = "", className = "") => {
		return `<img src="/img/${src}" alt="${alt}" class="${className}" loading="lazy" decoding="async">`;
	}
};
