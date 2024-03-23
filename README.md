# Whatsapp Web Clone Backend
You can find the user interface here : https://github.com/Theo-Fourniez/websocket-chat-app-frontend .  
This project was made to learn Angular 16 and Spring boot 3. The goal was to create a app similar to WhatsApp Web.

# Functionality
* Real time messaging (using websockets)
* Delayed messaging, for example when a user sends a message to another offline user (using HTTP requests)
* Receiving web push in browser notifications when receiving messages while the app is not opened (using push API and service workers)
* Adding friends

# Dev environment
* IntelliJ IDEA
* Java 17
* H2 in memory database
  
# Used references : 
* https://simple-push-demo.vercel.app/ : to tinker with push notifications
* https://developer.mozilla.org/en-US/docs/Web/API/Push_API
* https://developer.mozilla.org/en-US/docs/Web/API/Notification
* https://angular.io/guide/service-worker-notifications
