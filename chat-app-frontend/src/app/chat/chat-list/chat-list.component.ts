import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ChatService } from '../chat.service';
import { AuthService } from '../../auth/auth.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact-list',
  templateUrl: './chat-list.component.html',
  styleUrls: ['./chat-list.component.css'],
  imports: [FormsModule, CommonModule],
  standalone: true,
})
export class ChatListComponent implements OnInit {
  chats: any[] = [];
  newContactUsername: String = '';
  userId: number | null = null;
  chatSubscription: Subscription | undefined;
  errorMessage: string = '';

  constructor(
    private chatService: ChatService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getUserIdFromToken();
    this.loadChats();
  }

  ngOnDestroy(): void {
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }
  }

  loadChats(): void {
    if (this.userId !== null) {
      this.chatSubscription = this.chatService.getChats(this.userId).subscribe({
        next: (response) => {
          this.chats = response || [];
        },
      });
    }
  }
  createChat(): void {
    if (this.newContactUsername.trim()) {
      this.chatService.createChat(this.newContactUsername).subscribe({
        next: (response) => {
          this.loadChats();
          this.newContactUsername = '';
        },
        error: (error) => {
          this.errorMessage = 'Failed to create chat. Please try again later.';
        },
      });
    }
  }

  goToPrivateChat(chatId: number): void {
    this.router.navigate(['/private-chat', chatId]);
  }

  goToPublicChat(): void {
    this.router.navigate(['/public-chat']);
  }

  getUsername(chat: any): string {
    return chat.firstUserId === this.userId
      ? chat.secondUserUsername
      : chat.firstUserUsername;
  }
}
