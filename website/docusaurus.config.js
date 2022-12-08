module.exports = {
  title: "Build Server Protocol",
  tagline:
    "Protocol for IDEs and build tools to communicate about compile, run, test, debug and more.",
  url: "https://build-server-protocol.github.io/",
  baseUrl: "/",
  staticDirectories: ["static"],
  organizationName: "build-server-protocol",
  projectName: "build-server-protocol.github.io",
  deploymentBranch: "gh-pages",
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
          path: "docs/",
          routeBasePath: "/",
          showLastUpdateAuthor: true,
          showLastUpdateTime: true,
          editUrl:
            "https://github.com/build-server-protocol/build-server-protocol/edit/master/website/docs",
          sidebarPath: require.resolve("./sidebars.js"),
        },
        blog: false,
        theme: {
          customCss: "src/css/customTheme.css",
        },
      },
    ],
  ],
  plugins: [
    [
      "@docusaurus/plugin-client-redirects",
      {
        fromExtensions: ["html"],
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
          to: "specification",
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
      links: [],
      copyright: "Copyright © 2022 Build Server Protocol",
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
