import { Component, OnInit } from '@angular/core';
import { ChatService } from '../chat.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { BackButtonComponent } from '../../components/back-button/back-button.component';

@Component({
  selector: 'app-private-chat',
  standalone: true,
  imports: [FormsModule, CommonModule, BackButtonComponent],
  templateUrl: './private-chat.component.html',
  styleUrls: ['./private-chat.component.css'],
})
export class PrivateChatComponent implements OnInit {
  messages: any[] = [];
  newMessage: string = '';
  chatId: number;
  userId!: number | null;

  constructor(
    private chatService: ChatService,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    this.chatId = Number(this.route.snapshot.paramMap.get('chatId'));
    this.userId = this.authService.getUserIdFromToken();
  }

  ngOnInit() {
    this.loadMessages();

    this.chatService.messagesSubject.subscribe((message: string) => {
      this.messages.push(message);
    });

    this.chatService.connectToPrivateChat(this.chatId);
  }

  ngOnDestroy() {
    this.chatService.disconnect();
  }

  loadMessages(): void {
    this.chatService.getMessages(this.chatId).subscribe((response) => {
      this.messages = response;
    });
  }

  sendMessage() {
    if (this.newMessage.trim()) {
      this.chatService.sendPrivateMessage({
        content: this.newMessage,
        chatId: this.chatId,
        senderId: this.userId,
      });
      this.newMessage = '';
    }
  }

  trackById(index: number, message: any): number {
    return message.id;
  }
}
