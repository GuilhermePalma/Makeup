<div align="center">

   <h1 id="title">💄 API - Maquiagem</h1>

<h3>💄 Busca de Maquiagens na <a href="http://makeup-api.herokuapp.com" tagert="_blank">API - Makeup</a></h3>
<a href="#icons">
<img alt="Quantiade de Linguagens do Repositorio" src="https://img.shields.io/github/languages/count/GuilhermePalma/Makeup?color=2304D361">
</a>

   <a href="https://github.com/GuilhermePalma/Makeup">
      <img alt="Tamanho do Respositorio" src="https://img.shields.io/github/repo-size/GuilhermePalma/Makeup">
   </a>

   <a href="https://github.com/GuilhermePalma/Makeup/commits/main">
      <img alt="Ultimo Commit" src="https://img.shields.io/github/last-commit/GuilhermePalma/Makeup">
   </a>

   <a href="LICENSE">
      <img alt="Licensa" src="https://img.shields.io/github/license/GuilhermePalma/Makeup">
   </a>

<h4>🚧  Em Desenvolvimento 🚀 🚧</h4>

   <p>
      <img alt="Imagem da Tela Principal" title="Inicio" src="./printscreen/LightMode/Index.jpeg" width="200px">
      <img alt="Imagem do Menu de Opções" title="Menu" src="./printscreen/LightMode/MenuOptions.jpeg" width="200px">
      <img alt="Imagem da Tela com Dados de Pesquisa" title="CustomView" src="./printscreen/LightMode/CustomView.jpeg" width="200px">
      <img alt="Imagem da Tela de Resultados" title="ResultadosAPI" src="./printscreen/LightMode/ResultApi.jpeg" width="200px">
      <img alt="Imagem da Tela Localização Atual" title="Localizacao" src="./printscreen/LightMode/Location.jpeg" width="200px">
      <img alt="Imagem do Feedback da Localização" title="FeedbackLocalizacao" src="./printscreen/LightMode/LocationWithFragment.jpeg" width="200px">
      <img alt="Imagem do Sensor de Proximidade Longe" title="SensorLonge" src="./printscreen/LightMode/SensorProximity.jpeg" width="200px">
      <img alt="Imagem do Sensor de Proximidade Perto" title="SensorPerto" src="./printscreen/LightMode/SensorProximityOff.jpeg" width="200px">
   </p>

</div>

Tabela de conteúdos
=================
<!--ts-->
* [Sobre o projeto](#-sobre-o-projeto)
  * [Funcionalidades](#funcionalidades)
  * [Layout](#-layout)
    * [Mobile](#mobile)
  * [Como executar o projeto](#-como-executar-o-projeto)
    * [Pré-requisitos](#pré-requisitos)
  * [Tecnologias](#-tecnologias)
  * [Contribuidores](#-contribuidores)
  * [Como contribuir no projeto](#-como-contribuir-no-projeto-?)
<!--te-->


# 💻 Sobre o projeto

💄 **Makeup** - Projeto desenvolvido durante a materia Programação e Algoritimos Mobile II - ETEC 2021,
coordenado pela Professora [Aline F. Brito](https://github.com/alinefbrito).

:books: Esse aplicativo busca dados em uma API Externa de Maquiagem, Listando Imagem, Nome, Preço,
Tipo, Marca e Descrição de cada Produto. Tambem é Possivel favoritar Produtos, Obter a Localização
Atual e Criar uma Conta ou fazer Login

O Projeto utilizou a API Makeup. Para ver sua documentação, acesse [API Makeup](http://makeup-api.herokuapp.com/)

---

## Funcionalidades

- [x] Os usuarios podem pesquisar na API usando os filtros de:
  - [x] Marca
  - [x] Tags
  - [x] Tipo

- [x] A Aplicação ira exibir:
  - [X] Imagem
  - [x] Nome
  - [x] Preço
  - [x] Marca
  - [x] Tipo
  - [x] Descrição

- [x] Outros Recuros:
  - [X] Localização
  - [x] Tema Escuro

---


## 🎨 Layout

O Layout da Aplicação está Disponível no [Whimsical](https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm):

<a href="https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm">
  <img alt="Made by Guilherme Palma e Guilherme Callegari" src="https://img.shields.io/badge/Acessar%20Layout-Whimsical-green">
</a>

### Mobile

**Imagens** das Telas do APP - [Tema Normal](markdown/imagesNormalMode.md) e [Tema Escuro](markdown/imagesDarkMode.md)

**Video** Mostrando o Funcionamento do APP - [API - Makeup (Youtube)](https://youtu.be/WB9kvWjh3_g)


## 🚀 Como executar o projeto

Este projeto é divido em seis principais partes:
1. [Layout das Telas](app/src/main/res/layout/)
2. [Configurações das Telas](app/src/main/java/com/example/Makeup/view/activity)
3. [Elementos das Telas](app/src/main/java/com/example/Makeup/view)
4. [Funções do Sistema](app/src/main/java/com/example/Makeup/model)
5. [Layout Widget](app/src/main/res/layout/widget_app.xml)
6. [Configurações do Widget](app/src/main/java/com/example/Makeup/view/WidgetApp.java)

### Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
- [Git](https://git-scm.com) → Atualizações e Versionamento no Codigo
- [Android Studio](https://developer.android.com/studio/) → Editor da Google voltado ao Desenvolvimento Android


#### 📥 Baixando o Projeto

```bash

# Clone este repositório
$ git clone https://github.com/GuilhermePalma/Makeup.git

# Acesse a pasta do projeto no terminal/cmd
$ cd Makeup

```

---

## 🛠 Tecnologias

As seguintes ferramentas foram usadas na construção do projeto:
- **[Java](https://developer.android.com/docs)** → Linguagem Nativa de Desnvolvimento Android
- **[Postman](https://web.postman.co/)** → Testes da API Externa
- **[Swagger](https://editor.swagger.io/)** → Testes da API Localhost e Externa
- **[Winsical](https://whimsical.com/)**  →  **[Protótipação (Makeup)](https://whimsical.com/mobile-UmVv4wcQh1YZLBppfhptCm)**
- **[Freepik](https://www.freepik.com), [FlatIcon](https://www.flaticon.com/br) e [Pexels](https://www.pexels.com)** → Icones e Imagens

[Clique Aqui](markdown/references.md) para acessar as Referencias do Projeto

---

## 👨‍💻 Contribuidores

💜 Desenvolvedores que participaram desse Projeto :)

<table>
  <tr>
    <td align="center"><a href="https://github.com/GuilhermeCallegari"><img style="border-radius: 50%;" src="https://github.com/guilhermeCallegari.png" width="100px;" alt=""/><br /><sub><b>Guilherme Callegari</b></sub></a><br /><a href="https://github.com/GuilhermeCallegari" title="GitHub">🚀</a></td>
    <td align="center"><a href="https://github.com/guilhermepalma"><img style="border-radius: 50%;" src="https://github.com/guilhermePalma.png" width="100px;" alt=""/><br /><sub><b>Guilherme Palma</b></sub></a><br /><a href="https://github.com/guilhermepalma" title="Github">🚀</a></td>
  </tr>
</table>


## 💪 Como contribuir no projeto

[Clique Aqui](markdown/contribution.md) e veja como Contribuir nesse Projeto

---