import { Component, OnInit } from '@angular/core';
import { ChatService } from '../chat.service';
import { AuthService } from '../../auth/auth.service';
import { BackButtonComponent } from '../../components/back-button/back-button.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-public-chat',
  templateUrl: './public-chat.component.html',
  styleUrls: ['./public-chat.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, BackButtonComponent],
})
export class PublicChatComponent implements OnInit {
  messages: any[] = [];
  newMessage: string = '';
  userId!: number | null;

  constructor(
    private chatService: ChatService,
    private authService: AuthService
  ) {
    this.userId = this.authService.getUserIdFromToken();
  }

  ngOnInit(): void {
    this.loadMessages();

    this.chatService.messagesSubject.subscribe((message: any) => {
      this.messages.push(message);
    });

    this.chatService.connectToPublicChat();
  }

  loadMessages(): void {
    this.chatService.getPublicMessages().subscribe((response: any[]) => {
      this.messages = response;
    });
  }

  sendMessage(): void {
    if (this.newMessage.trim()) {
      this.chatService.sendMessage({
        content: this.newMessage,
        senderId: this.userId,
      });
      this.newMessage = '';
    }
  }

  trackById(index: number, message: any): number {
    return message.id;
  }
}
