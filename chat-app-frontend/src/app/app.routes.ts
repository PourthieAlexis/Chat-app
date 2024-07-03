import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { PrivateChatComponent } from './chat/private-chat/private-chat.component';
import { PublicChatComponent } from './chat/public-chat/public-chat.component';
import { authGuard } from './auth/auth.guard';
import { ChatListComponent } from './chat/chat-list/chat-list.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'chats',
    component: ChatListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'private-chat/:chatId',
    component: PrivateChatComponent,
    canActivate: [authGuard],
  },
  {
    path: 'public-chat',
    component: PublicChatComponent,
    canActivate: [authGuard],
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
