# GPS e Mapa com Jetpack Compose

Este projeto demonstra como integrar a funcionalidade de GPS e exibir um mapa usando Google Maps em um aplicativo Android com Jetpack Compose. O aplicativo obtém a localização atual do usuário e a exibe em um mapa, além de mostrar a localização com uma bolinha azul.

## Recursos

- Obtenção da localização atual do usuário.
- Exibição da localização do usuário em um mapa usando Google Maps.
- Configuração para otimizar o desempenho do mapa (modo mais leve). (**Pendente**)
- Interface de usuário com Jetpack Compose.

## Configuração do Projeto

### Requisitos

- Android Studio
- Kotlin
- Jetpack Compose
- Google Maps API Key

### Configuração do Google Maps

1. **Obtenha uma chave da API do Google Maps**:
   - Vá para o [Console de APIs do Google](https://console.cloud.google.com/).
   - Crie um novo projeto ou use um projeto existente.
   - Ative a API do Google Maps Android.
   - Gere uma chave da API e adicione-a ao seu arquivo `AndroidManifest.xml`.

2. **Adicionar a chave da API ao arquivo `AndroidManifest.xml`**:

    ```xml
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="com.example.gpsmap">

        <application
            ...>
            <meta-data
                android:name="com.google.android.geo.API_KEY"
                android:value="YOUR_API_KEY_HERE"/>
            ...
        </application>
    </manifest>
    ```
