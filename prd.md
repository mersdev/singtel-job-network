
Product Name: Singtel Business Network On-Demand (MVP)

**1. Introduction & Vision**

**Vision:** To empower businesses with the ability to manage their network infrastructure with the same speed, agility, and on-demand control as modern cloud services. We are moving away from traditional, slow-moving telecommunication service delivery towards an automated, API-driven, and customer-centric model.

**Product Description:** The Singtel Business Network On-Demand platform is a self-service web portal that leverages Singtel's investment in Network as a Service (NaaS), Service Orchestration (SO), and 5G technology. This MVP will provide Small and Medium-sized Enterprises (SMEs) with the initial tools to instantly order, configure, and monitor their essential network services, reducing provisioning times from weeks to minutes.

---

**2. Company Background & Use Case**

**Background:** As a leading communications technology group in Asia, Singtel is undergoing a significant digitalization transformation. A key part of this is modernizing the Group Technology and Networks division to be more agile and responsive to market needs. This product directly supports that strategy.

**Problem Statement:** Currently, SMEs face significant friction when trying to procure or modify enterprise-grade network services. The process often involves lengthy sales cycles, manual paperwork, and scheduled appointments with technicians, leading to delays and inflexibility. For a modern business, the inability to quickly scale bandwidth for a product launch or set up a secure connection for a new branch office is a major operational bottleneck.

**Use Case:** An e-commerce SME in Singapore is preparing for the "11.11" mega-sale. They anticipate a 500% increase in web traffic and need to scale their internet bandwidth to ensure their website remains responsive. Traditionally, this would require contacting their Singtel account manager weeks in advance. With the "Business Network On-Demand" portal, the IT manager can log in, select their current internet service, and schedule a temporary bandwidth increase for the sale period with just a few clicks. The change is automatically provisioned and then scaled back down post-event, with billing adjusted automatically.

---

**3. MVP Features (Minimum Viable Product)**

**Feature 1: Self-Service Customer Portal & Service Catalog**
*   **Description:** A secure, authenticated web portal for business customers. Upon login, the user is presented with a clear dashboard and a catalog of available network services (e.g., Business Fibre Broadband, Secure VPN Access).
*   **User Story:** "As an office manager, I want to be able to log in to a simple portal, see the network services we are currently subscribed to, and browse a catalog of new services I can order for our company."
*   **Technical Implementation Notes:**
    *   **Frontend:** VueJS or AngularJS for a responsive user interface.
    *   **Backend:** .NET Core or Java Spring Boot microservice for user authentication, session management, and retrieving customer/service data.
    *   **Database:** SQL Server or MySQL to store user profiles and company information.

**Feature 2: Automated Service Provisioning**
*   **Description:** The core feature of the MVP. Enables a customer to select a service from the catalog (e.g., a new 500 Mbps Business Fibre line), specify parameters (e.g., installation address), and place an order. The backend systems will then automate the entire provisioning workflow.
*   **User Story:** "As a startup founder opening a new office, I want to order a new internet connection through the portal and have it activated automatically within hours, not weeks, so my team can be productive immediately."
*   **Technical Implementation Notes:**
    *   **Backend:** A RESTful API endpoint (built with ASP.NET or Spring Boot) will receive the order.
    *   **Orchestration:** This API will trigger a workflow in the central Service Orchestrator (SO). The SO, communicating via the Network Service Bus (NSB), will send automated commands to configure network elements, update inventory systems, and notify billing. This demonstrates the NaaS architecture.
    *   **CI/CD:** The microservices handling these requests will be managed via a CI/CD pipeline (e.g., Jenkins, Azure DevOps) to ensure rapid updates and reliability.

**Feature 3: On-Demand Bandwidth Management**
*   **Description:** For eligible services, customers can dynamically adjust their bandwidth up or down through a simple slider or input field in the portal. The change is applied in near-real-time.
*   **User Story:** "As an IT Manager, I want to log into the portal and temporarily increase our primary internet line's bandwidth from 500 Mbps to 1 Gbps during our annual company-wide video conference, and then easily scale it back down to save costs."
*   **Technical Implementation Notes:**
    *   **Frontend:** An interactive UI component (e.g., slider) built with VueJS/AngularJS.
    *   **Backend:** A dedicated microservice that exposes a RESTful API (e.g., `PUT /api/services/{service-id}/bandwidth`).
    *   **Orchestration:** The API call triggers a specific, pre-defined workflow in the Service Orchestrator that reconfigures the network port policies associated with the customer's service. This is a prime example of Network as a Service.

**Feature 4: Basic Service Monitoring Dashboard**
*   **Description:** A dashboard widget that provides customers with a near-real-time view of their subscribed services, including status (Up/Down), current bandwidth utilization, and basic uptime statistics.
*   **User Story:** "As a business owner, when my team complains the internet is slow, I want to be able to check a dashboard to see our current usage and confirm the service is online before I call support."
*   **Technical Implementation Notes:**
    *   **Data Collection:** A backend service will poll network monitoring systems or subscribe to event streams to get telemetry data.
    *   **Backend API:** A microservice will process this data and expose a simple endpoint (e.g., `GET /api/services/{service-id}/metrics`).
    *   **Frontend:** The dashboard will use a library like Chart.js (integrated into Vue/Angular) to visualize the data received from the API.

---

**4. Required Technology Stack Summary**

*   **Programming Languages & Frameworks:** .NET Core, C#, Java, Spring Boot
*   **Frontend:** VueJS, AngularJS, HTML5, CSS3
*   **Backend Architecture:** Microservices, RESTful Web Services
*   **Databases:** MS SQL Server, MySQL
*   **DevOps & Cloud:** CI/CD Pipelines, Docker/Kubernetes, hosted on AWS or Microsoft Azure.
*   **Domain Specific Tech:** Integration with Network Service Bus (NSB) and Service Orchestrator (SO) platforms via REST APIs.

