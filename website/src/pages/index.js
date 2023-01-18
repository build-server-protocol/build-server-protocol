import Layout from "@theme/Layout";
import React from "react";
import Link from "@docusaurus/Link";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";

const Features = () => {
  const features = [
    {
      title: "What is BSP?",
      content:
        "The Build Server Protocol (BSP) provides endpoints for IDEs and build tools to communicate about directory layouts, external dependencies, compile, test and more.",
      image: "https://i.imgur.com/LmRwu2s.png",
      imageAlign: "left",
    },
    {
      title: "Run, test and debug",
      content:
        "Example of running, testing and debugging a Scala program in VS Code via BSP and the Debug Adapter Protocol.",
      image:
        "https://user-images.githubusercontent.com/3709537/65522506-2622b380-deeb-11e9-821b-f7e43aec5305.gif",
      imageAlign: "right",
    },
    {
      title: "Rich compilation model",
      content:
        "Example of compiling multiple projects in IntelliJ via BSP. " +
        "The build server can notify the client about compile progress and report compile errors.",
      image: "https://i.imgur.com/aE6fSyb.gif",
      imageAlign: "left",
    },
    {
      title: "LSP-inspired",
      content:
        "The Build Server Protocol (BSP) is complementary to the Language Server Protocol (LSP). " +
        "While LSP allows editor clients to abstract over different programming languages, BSP allows IDE clients to abstract over different build tools.",
      image: "https://i.imgur.com/3stUaOx.png",
      imageAlign: "right",
    },
  ];
  return (
    <div>
      {features.map((feature) => (
        <div className="hero text--center" key={feature.title}>
          <div
            className={`container ${
              feature.imageAlign === "right" ? "flex-row" : "flex-row-reverse"
            }`}
          >
            <div className="padding--md">
              <h2 className="hero__subtitle">{feature.title}</h2>
              <p>{feature.content}</p>
            </div>
            <div className="padding-vert--md">
              <img src={feature.image} />
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

const Index = () => {
  const { siteConfig } = useDocusaurusContext();
  return (
    <Layout>
      <div className="hero text--center">
        <div className="container">
          <div className="inner">
            <h1 className="hero__title">{siteConfig.title}</h1>
            <p className="hero__subtitle">{siteConfig.tagline}</p>
          </div>
          <div>
            <Link
              to="/docs/specification"
              className="button button--lg button--outline button--primary margin--sm"
            >
              Get Started
            </Link>
            <Features />
          </div>
        </div>
      </div>
    </Layout>
  );
};

export default Index;
