import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Client, Stomp } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  private baseUrl: string = 'http://localhost:8080';
  private stompClient: Client | undefined;
  public messagesSubject: Subject<any> = new Subject();

  constructor(private http: HttpClient) {}

  private connect(callback: () => void) {
    const socket = new SockJS(`${this.baseUrl}/ws`);
    this.stompClient = Stomp.over(socket);

    this.stompClient.onConnect = () => {
      callback();
    };

    this.stompClient.activate();
  }

  connectToPublicChat() {
    this.connect(() => {
      this.stompClient?.subscribe('/topic/public', (message) => {
        this.messagesSubject.next(JSON.parse(message.body));
      });
    });
  }

  connectToPrivateChat(chatId: number) {
    this.connect(() => {
      this.stompClient?.subscribe(
        `/user/${chatId}/queue/private`,
        (message) => {
          this.messagesSubject.next(JSON.parse(message.body));
        }
      );
    });
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
  }

  sendMessage(message: any) {
    if (this.stompClient) {
      this.stompClient.publish({
        destination: '/app/sendMessage',
        body: JSON.stringify(message),
      });
    }
  }

  sendPrivateMessage(message: any) {
    if (this.stompClient) {
      this.stompClient.publish({
        destination: '/app/privateMessage',
        body: JSON.stringify(message),
      });
    }
  }

  getMessages(chatId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/messages/${chatId}`);
  }

  getPublicMessages(): Observable<any> {
    return this.http.get(`${this.baseUrl}/messages`);
  }

  createChat(recipientUsername: String): Observable<any> {
    return this.http.post(`${this.baseUrl}/chats`, recipientUsername);
  }

  getChat(chatId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/chats/${chatId}`);
  }

  getChats(userId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/users/${userId}/chats`);
  }
}
