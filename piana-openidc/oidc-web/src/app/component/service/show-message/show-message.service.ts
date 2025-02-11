import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ShowMessageService {
  private _messages: MessageModel[];
  private _messagesSubject: BehaviorSubject<MessageModel>;
  private snackBarRef = inject(MatSnackBar);

  private durationInSeconds = 5;

  constructor() {
    this._messages = [];
    this._messagesSubject = new BehaviorSubject<MessageModel>(null);
   }

   public get message(): Observable<MessageModel> {
    return this._messagesSubject.asObservable();
   }

   public addMessage(messageModel: MessageModel) {
    // console.log(messageModel)

    this._messages.push(messageModel);
    this._messagesSubject.next(this._messages[0]);
   }

   public removeMessage() {
    console.log('remove', 0)
    this._messages.splice(0, 1);
    this._messagesSubject.next(this._messages[0]);
   }
}

export interface MessageModel {
  messageType: MessageType,
  title: string,
  description: string
}

export enum MessageType {
  SUCCESS = 1,
  INFO,
  ERROR
}