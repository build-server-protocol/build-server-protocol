const React = require("react");

const siteConfig = require(process.cwd() + "/siteConfig.js");

class Footer extends React.Component {
  render() {
    const {
      baseUrl,
      copyright,
      colors: { secondaryColor }
    } = this.props.config;
    const docsUrl = `${baseUrl}docs/`;
    return (
      <footer
        className="nav-footer"
        id="footer"
        style={{ backgroundColor: secondaryColor }}
      >
        <section className="sitemap">
          {this.props.config.footerIcon && (
            <a href={this.props.config.baseUrl} className="nav-home">
              <img
                src={`${this.props.config.baseUrl}${this.props.config.footerIcon}`}
                alt={this.props.config.title}
                width="66"
                height="58"
              />
            </a>
          )}
          <div>
            <h5>Overview</h5>
            <a href={`${docsUrl}specification.html`}>Specification</a>
            <a href={`${docsUrl}implementations.html`}>Implementations</a>
          </div>
          <div>
            <h5>Social</h5>
            <a
              href="https://github.com/build-server-protocol/build-server-protocol"
              target="_blank"
            >
              <img src="https://img.shields.io/github/stars/scalacenter/bsp.svg?color=%23087e8b&label=stars&logo=github&style=social" />
            </a>
            <a href="https://discord.gg/7tMENrnv8p" target="_blank">
              <img src="https://badgen.net/badge/icon/discord?icon=discord&label" />
            </a>
          </div>
        </section>
        <section className="copyright">{copyright}</section>
        <section className="copyright">
          <p>
            The Build Server Protocol is a collaborative effort between
            developers at the <a href="https://scala.epfl.ch/">Scala Center</a>{" "}
            and <a href="https://www.jetbrains.com/">Jetbrains</a>.
          </p>
        </section>
      </footer>
    );
  }
}

module.exports = Footer;
