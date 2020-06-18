# AWS::Kendra::Index

This package contains the handlers that CloudFormation invokes when provisioning Kendra Indexes.

This package has two main components:
1. The JSON schema describing a Kendra Index, `aws-kendra-index.json`
1. The resource handlers that actually create, delete, update, read and list Kendra Indexes.

The RPDK will automatically generate the correct resource model from the schema whenever the project is built via Maven. You can also do this manually with the following command: `cfn generate`.

> Please don't modify files under `target/generated-sources/rpdk`, as they will be automatically overwritten.

The code uses [Lombok](https://projectlombok.org/), and [you may have to install IDE integrations](https://projectlombok.org/setup/overview) to enable auto-complete for Lombok-annotated classes.
