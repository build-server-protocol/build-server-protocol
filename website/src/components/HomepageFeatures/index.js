import React from "react";
import clsx from "clsx";
import styles from "./styles.module.css";

const FeatureList = [
  {
    title: "What is BSP?",
    description:
      "The Build Server Protocol (BSP) provides endpoints for IDEs and build tools to communicate about directory layouts, external dependencies, compile, test and more.",
    image: require("@site/static/img/bsp-workflow.png").default,
    imageAlign: "left",
  },
  {
    title: "Run, test and debug",
    description:
      "Example of running, testing and debugging a Scala program in VS Code via BSP and the Debug Adapter Protocol.",
    image: require("@site/static/img/demo.gif").default,
    imageAlign: "right",
  },
  {
    title: "Rich compilation model",
    description:
      "Example of compiling multiple projects in IntelliJ via BSP. " +
      "The build server can notify the client about compile progress and report compile errors.",
    image: require("@site/static/img/compilation-model.gif").default,
    imageAlign: "left",
  },
  {
    title: "LSP-inspired",
    description:
      "The Build Server Protocol (BSP) is complementary to the Language Server Protocol (LSP). " +
      "While LSP allows editor clients to abstract over different programming languages, BSP allows IDE clients to abstract over different build tools.",
    image: require("@site/static/img/bsp_lsp.png").default,
    imageAlign: "right",
  },
];

function Feature({ image, title, description, imageAlign }) {
  return (
    <div className={clsx("col col--4")}>
      <div className="text--center">
        <img src={image} alt={title} />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
