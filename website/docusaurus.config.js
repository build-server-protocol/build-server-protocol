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
          path: "../website/target/docs",
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
          editUrl: ({ docPath }) =>
            `https://github.com/build-server-protocol/build-server-protocol/edit/master/docs/${docPath}`,
          sidebarPath: "../website/sidebars.json",
        },
        blog: {},
        theme: {
          customCss: "./src/css/customTheme.css",
        },
      },
    ],
  ],
  plugins: [
    [
      "@docusaurus/plugin-client-redirects",
      {
        fromExtensions: ["html"],
        redirects: [
          {
            from: "/build-server-protocol/",
            to: "/",
          },
        ],
      },
    ],
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
              to: "docs/implementations",
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
    algolia: {
      // The application ID provided by Algolia
      appId: "AL7XHCGLS5",

      // Public API key: it is safe to commit it
      apiKey: "75a446641843ac7e9203131fdba3e756",
      indexName: "build-server-protocol",
      contextualSearch: true,
      searchPagePath: "search",
    },
  },
};
