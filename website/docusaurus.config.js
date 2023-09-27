const { resolve } = require("path");
module.exports = {
  title: "Build Server Protocol",
  tagline:
    "Protocol for IDEs and build tools to communicate about compile, run, test, debug and more.",
  url: "https://build-server-protocol.github.io",
  baseUrl: "/",
  organizationName: "build-server-protocol",
  projectName: "build-server-protocol.github.io",
  deploymentBranch: "master",
  favicon: "img/favicon.ico",
  customFields: {
    blogSidebarCount: "ALL",
    repoUrl: "https://github.com/build-server-protocol/build-server-protocol",
  },
  onBrokenLinks: "log",
  onBrokenMarkdownLinks: "log",
  presets: [
    [
      "@docusaurus/preset-classic",
      {
        docs: {
          path: "generated",
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
          editUrl: ({ docPath }) =>
            `https://github.com/build-server-protocol/build-server-protocol/edit/master/docs/${docPath}`,
          sidebarPath: "sidebars.json",
        },
        blog: {},
        theme: {
          customCss: "./src/css/customTheme.css",
        },
      },
    ],
  ],
  plugins: [
    () => ({
      name: "resolve-react",
      configureWebpack() {
        return {
          resolve: {
            alias: {
              react: resolve("node_modules/react"),
            },
          },
        };
      },
    }),
    [
      "@docusaurus/plugin-client-redirects",
      {
        fromExtensions: ["html"],
        redirects: [
          {
            from: "/build-server-protocol",
            to: "/",
          },
          {
            from: "/docs/bsp",
            to: "/docs/specification",
          },
          {
            from: "/docs/faq",
            to: "/docs/overview/faq",
          },
          {
            from: "/docs/implementations",
            to: "/docs/overview/implementations",
          },
          {
            from: "/docs/server-discovery",
            to: "/docs/overview/server-discovery",
          },
        ],
      },
    ],
    "@easyops-cn/docusaurus-search-local",
  ],
  themeConfig: {
    navbar: {
      title: "Build Server Protocol",
      logo: {
        src: "img/bsp-logo.svg",
      },
      items: [
        {
          to: "docs/specification",
          label: "Specification",
          position: "left",
        },
        {
          href: "https://github.com/build-server-protocol/build-server-protocol",
          label: "GitHub",
          position: "left",
        },
      ],
    },
    image: "img/bsp-logo.svg",
    footer: {
      links: [
        {
          title: "Overview",
          items: [
            {
              label: "Specification",
              to: "docs/specification",
            },
            {
              label: "Implementations",
              to: "docs/overview/implementations",
            },
          ],
        },
        {
          title: "Social",
          items: [
            {
              html: `<a href="https://github.com/build-server-protocol/build-server-protocol" target="_blank">
                      <img src="https://img.shields.io/github/stars/build-server-protocol/build-server-protocol.svg?color=%23087e8b&label=stars&logo=github&style=social" />
                    </a>`,
            },
            {
              html: `<a href = "https://discord.gg/7tMENrnv8p" target = "_blank" >
                      <img src="https://img.shields.io/discord/697002009336873011?logo=discord&style=social" />
                    </a>`,
            },
          ],
        },
      ],
      copyright: "Copyright © 2023 Build Server Protocol",
      logo: {
        src: "img/bsp-logo.svg",
      },
    },
  },
};
