# CZERTAINLY Common Credential Provider

> This repository is part of the commercial open-source project CZERTAINLY, but the connector is available under subscription. 
> You can find more information about the project at [CZERTAINLY](https://github.com/3KeyCompany/CZERTAINLY) repository, including the contribution guide.

Common Credential Provider is the implementation of basic credential types and validation interfaces. This connector provides options to add the credential to the Core which can be used within other objects in the platform for authentication and authorization purposes.

> Credentials provided by the Common Credential Provider are not involved in
the platform authentication and authorization process.

Common Credential Provider implement the following credential types:
- Basic (Username and Password)
- API Key
- Software KeyStore (Softkeys, i.e., certificate related authentication)

## Short Process Description

The credential can be created and added to the Core based on their type. Selecting the specific type of credential and providing the necessary information is the first step in the process.  When the connector or other platform object requests the specific type of the credential implemented in this Credential Provider, it will provide its values.

To know more about the Core, refer to [CZERTAINLY Core](https://github.com/3KeyCompany/CZERTAINLY-Core).

## Interfaces

Common Credential Provider implements the Credential Provider Interface from the CZERTAINLY Interfaces. To learn more about the interfaces and end points, refer to the [CZERTAINLY Interfaces](https://github.com/3KeyCompany/CZERTAINLY-Interfaces).

For more information regarding the credentials, please refer to the CZERTAINLY documentation.