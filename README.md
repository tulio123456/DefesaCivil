# 🚨 Sistema de Alertas — Android + Painel de Computador

## O que este projeto faz

Você terá:

COMPUTADOR
   ↓
Painel web local
   ↓
Firebase Cloud Messaging (FCM)
   ↓
ANDROID
   ↓
Notificação + vibração + som

O Android se inscreve automaticamente no tópico:
`defesa_civil_testes`

O computador envia uma mensagem para esse tópico.

## IMPORTANTE

Este é um projeto independente para TESTES/SIMULAÇÕES.

Ele NÃO é a Defesa Civil, NÃO é o Defesa Civil Alerta oficial e NÃO consegue fabricar mensagens Cell Broadcast.

Os alertas oficiais do Governo continuam sendo recebidos pelo sistema de Alertas de Emergência do Android/rede móvel. O aplicativo oferece um botão para abrir essa configuração e um link para o IDAP.

## 1. Criar o projeto Firebase

1. Entre no Firebase Console.
2. Crie um projeto.
3. Adicione um aplicativo Android.
4. Use exatamente este package name:
   `br.com.defesacivil.alerta`
5. Baixe `google-services.json`.
6. Coloque o arquivo em:
   `app/google-services.json`

O arquivo é fornecido pelo Firebase para configurar o aplicativo Android.

## 2. Habilitar o Firebase Cloud Messaging

No projeto Firebase, habilite o Cloud Messaging/API conforme a documentação atual do Firebase.

## 3. Criar credencial para o computador

No Firebase/Google Cloud, crie uma Service Account com permissão para enviar mensagens FCM.

Baixe o JSON da Service Account.

RENOMEIE para:
`service-account.json`

Coloque dentro de:
`sender/service-account.json`

NUNCA publique esse arquivo, envie para GitHub ou coloque dentro do APK.

## 4. Compilar o Android

Abra a pasta no Android Studio.

Coloque:
`google-services.json`
em:
`app/google-services.json`

Depois:
- Sync Project with Gradle Files
- Run

Instale no Android.

Na primeira execução, permita notificações.

O aplicativo vai aparecer como conectado ao canal de testes.

## 5. Rodar o painel no Windows

Instale Node.js.

Abra o PowerShell dentro da pasta `sender`.

Execute:

npm install

Depois:

npm start

Aparecerá:

Painel aberto em http://127.0.0.1:8787

Abra no navegador:
http://127.0.0.1:8787

## 6. Enviar

Preencha:
- Tipo
- Nível
- Título
- Mensagem

Clique:

ENVIAR PARA O CELULAR

O Firebase entregará a mensagem ao Android inscrito no tópico.

## 7. O celular pode estar longe do computador?

SIM.

O computador e o Android não precisam estar na mesma Wi-Fi quando você usa FCM.

Ambos precisam ter conexão com a internet.

## 8. App fechado

O FCM permite que notificações sejam entregues quando o aplicativo está em segundo plano. O sistema Android coloca a notificação na bandeja em determinados tipos de mensagem.

## 9. Sobre "alerta oficial"

Não existe neste projeto uma função para você escrever uma mensagem no computador e transformá-la em "alerta oficial da Defesa Civil".

O Cell Broadcast oficial é uma infraestrutura diferente.

Use:
- Alertas de emergência do próprio Android para o canal oficial;
- IDAP para consultar informações oficiais;
- este painel apenas para seus testes/simulações.

## 10. Solução de problemas

Se o Android não receber:
1. confira a internet;
2. confirme que as notificações estão permitidas;
3. abra o app pelo menos uma vez;
4. confirme que `google-services.json` pertence ao mesmo Firebase project usado pelo `service-account.json`;
5. confira se o Cloud Messaging está habilitado;
6. confirme que o app aparece como inscrito no canal.

Se o painel não iniciar:
- confira se Node.js está instalado;
- confira se `sender/service-account.json` existe;
- execute `npm install`;
- depois `npm start`.

## Estrutura

DefesaCivilAlertaCompleto/
├── app/
│   ├── google-services.json  <-- você coloca
│   └── src/
├── sender/
│   ├── public/
│   │   └── index.html
│   ├── service-account.example.json
│   ├── package.json
│   └── server.js
├── build.gradle
├── settings.gradle
└── README.md
