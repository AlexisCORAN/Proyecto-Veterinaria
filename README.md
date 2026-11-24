# Proyecto-Veterinaria — Veterinary Clinic Management System

Desktop management application built in **Java (Swing)** following a strict **Model-View-Controller (MVC)** architecture. Designed to manage veterinary clinic operations—including clients, pets, medical staff, services, and appointment scheduling—using **custom-built linear and non-linear data structures** and **binary file serialization** for persistent storage without relying on external database engines.

## 🚀 Key Technical Highlights

* **Zero External Database Dependencies:** Implements custom persistence managers (`AlmacenarCitas`, `AlmacenarCliente`, `AlmacenarMascota`, `AlmacenarPersonal`, `AlmacenarServicio`) using Java Object Serialization (`.bin` files) to store and retrieve system state locally.
* **Custom Data Structures (Built from Scratch):** Instead of relying solely on standard Java collections, core business entities are managed through custom-engineered data structures:
  * **Binary Search Trees (`ArbolCitas`):** Efficient indexing, searching, and hierarchical organization of scheduled appointments[cite: 5].
  * **Queues (`ColaCitas`):** FIFO (First-In, First-Out) management for active waiting-room appointment queues[cite: 5].
  * **Stacks (`PilaCitas`):** LIFO (Last-In, First-Out) tracking for recently attended appointments and history rollbacks[cite: 5].
  * **Singly & Doubly Linked Lists (`ListaClientes`, `ListaMascotas`, `ListaPersonal`):** Dynamic memory allocation and bidirectional traversal for client, pet, and staff records[cite: 5].
  * **Custom Sorting & Arrays (`OrdenarFechas`, `ArregloUsuarios`, `ListaServicios`):** Chronological appointment sorting and user authentication management[cite: 5].
* **Clean MVC Architecture:** Strict separation of concerns across `Entidades` (Models), `Vista` (Java Swing GUI panels & modal dialogs), `Controlador` (Event listeners & UI controllers), and `Procesos` (Core business logic & automated code generation)[cite: 5].

## 🛠️ Tech Stack

* **Language:** Java (JDK)
* **GUI Framework:** Java Swing (`JFrame`, `JDialog`, modular `JPanel` views)[cite: 5]
* **Storage:** Binary File Serialization (`java.io.Serializable`, `.bin`)[cite: 5]
* **Build & IDE:** Apache Ant (`build.xml`), NetBeans IDE[cite: 5]

## 📁 Project Architecture

```text
Proyecto-Veterinaria/
├── src/
│   ├── Arboles/                 # Binary Search Tree implementation (ArbolCitas)
│   ├── Arreglos/                # Static array structures (ArregloUsuarios)
│   ├── Colas/                   # FIFO Queue implementation (ColaCitas)
│   ├── Pilas/                   # LIFO Stack implementation (PilaCitas)
│   ├── ListaEnlazadas/          # Singly Linked List (ListaClientes)
│   ├── ListasDoblesEnlazadas/   # Doubly Linked Lists (ListaMascotas, ListaPersonal)
│   ├── EstructuraArrayList/     # Dynamic list wrappers (ListaServicios)
│   ├── Ordenamientos/           # Date sorting algorithms (OrdenarFechas)
│   ├── Entidades/               # Domain models (Cita, Cliente, Mascota, Persona, Personal, Servicio, Usuario)
│   ├── Persistencia/            # Binary file read/write handlers (*.bin)
│   ├── Procesos/                # Business rules, validations & ID generators
│   ├── Controlador/             # MVC Controllers binding views to data structures
│   ├── Vista/                   # Swing Forms (frmLogin, frmMenu) & CRUD Panels/Dialogs
│   └── Principal/               # Application entry point (Main.java)
├── build.xml                    # Ant build script
└── .gitignore                   # Ignores compiled *.bin data files and build artifacts
