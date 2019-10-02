/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require("react");

const CompLibrary = require("../../core/CompLibrary.js");
const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

const siteConfig = require(process.cwd() + "/siteConfig.js");

function docUrl(doc, language) {
  return siteConfig.baseUrl + "docs/" + (language ? language + "/" : "") + doc;
}

class Button extends React.Component {
  render() {
    return (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={this.props.href} target={this.props.target}>
          {this.props.children}
        </a>
      </div>
    );
  }
}

Button.defaultProps = {
  target: "_self"
};

const SplashContainer = props => (
  <div className="homeContainer">
    <div className="homeSplashFade">
      <div className="wrapper homeWrapper">{props.children}</div>
    </div>
  </div>
);

const ProjectTitle = props => (
  <h2 className="projectTitle">
    {siteConfig.title}
    <small>{siteConfig.tagline}</small>
  </h2>
);

const PromoSection = props => (
  <div className="section promoSection">
    <div className="promoRow">
      <div className="pluginRowBlock">{props.children}</div>
    </div>
  </div>
);

class HomeSplash extends React.Component {
  render() {
    let language = this.props.language || "";
    return (
      <SplashContainer>
        <div className="inner">
          <ProjectTitle />
          <PromoSection>
            <Button href={docUrl("specification", language)}>
              Get Started
            </Button>
          </PromoSection>
        </div>
      </SplashContainer>
    );
  }
}

const Block = props => (
  <Container
    padding={["bottom", "top"]}
    id={props.id}
    background={props.background}
  >
    <GridBlock align="left" contents={props.children} layout={props.layout} />
  </Container>
);

const Features = props => {
  const features = [
    {
      title: "What is BSP?",
      content:
        "The Build Server Protocol (BSP) provides endpoints for IDE and build tools to communicate about compiling, running, testing and debugging programs.",
      image: "https://i.imgur.com/LmRwu2s.png",
      imageAlign: "left"
    },
    {
      title: "Rich compilation model",
      content: "Example of compiling multiple projects in IntelliJ via BSP.",
      image: "https://i.imgur.com/aE6fSyb.gif",
      imageAlign: "right"
    },
    {
      title: "Run, test and debug",
      content:
        "Example of running, testing and debugging a Scala program in VS Code via BSP and the Debug Adapter Protocol.",
      image:
        "https://user-images.githubusercontent.com/3709537/65522506-2622b380-deeb-11e9-821b-f7e43aec5305.gif",
      imageAlign: "left"
    },
    {
      title: "LSP-inspired",
      content:
        "The Build Server Protocol (BSP) is complementary to the Language Server Protocol (LSP). " +
        "While LSP allows editor clients to abstract over different programming languages, BSP allows IDE clients to abstract over different build tools.",
      image: "https://i.imgur.com/BIF8iHt.png",
      imageAlign: "right"
    }
  ];
  return (
    <div
      className="productShowcaseSection paddingBottom"
      style={{ textAlign: "left" }}
    >
      {features.map(feature => (
        <Block key={feature.title}>{[feature]}</Block>
      ))}
    </div>
  );
};
class Index extends React.Component {
  render() {
    let language = this.props.language || "";

    return (
      <div>
        <HomeSplash language={language} />
        <Features />
      </div>
    );
  }
}

module.exports = Index;
