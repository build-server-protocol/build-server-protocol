// See https://docusaurus.io/docs/site-config.html for all the possible
// site configuration options.

const repoUrl =
  "https://github.com/build-server-protocol/build-server-protocol";
const baseUrl = "/";

const siteConfig = {
  title: "Build Server Protocol",
  tagline:
    "Protocol for IDEs and build tools to communicate about compile, run, test, debug and more.",
  url: "https://build-server-protocol.github.io/build-server-protocol",
  baseUrl: baseUrl,

  // Used for publishing and more
  projectName: "build-server-protocol.github.io",
  organizationName: "build-server-protocol",

  // algolia: {
  //   apiKey: "c865f6d974a3072a35d4b53d48ac2307",
  //   indexName: "metals"
  // },

  // gaTrackingId: "UA-140140828-1",

  // For no header links in the top nav bar -> headerLinks: [],
  headerLinks: [
    { doc: "specification", label: "Specification" },
    { href: repoUrl, label: "GitHub", external: true }
  ],

  // If you have users set above, you add it here:
  // users,

  /* path to images for header/footer */
  headerIcon: "img/bsp-logo.svg",
  footerIcon: "img/bsp-logo.svg",
  favicon: "img/favicon.ico",

  /* colors for website */
  colors: {
    primaryColor: "#444",
    secondaryColor: "#555"
  },

  customDocsPath: "website/target/docs",

  // stylesheets: [baseUrl + "css/custom.css"],

  blogSidebarCount: "ALL",

  // This copyright info is used in /core/Footer.js and blog rss/atom feeds.
  copyright: `Copyright © ${new Date().getFullYear()} Build Server Protocol`,

  highlight: {
    // Highlight.js theme to use for syntax highlighting in code blocks
    theme: "github"
  },

  /* On page navigation for the current documentation page */
  onPageNav: "separate",

  /* Open Graph and Twitter card images */
  ogImage: "img/bsp-logo.svg",
  twitterImage: "img/bsp-logo.svg",

  editUrl: `${repoUrl}/edit/master/docs/`,

  repoUrl
};

module.exports = siteConfig;
