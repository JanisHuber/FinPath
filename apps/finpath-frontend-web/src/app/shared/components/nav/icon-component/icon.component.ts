import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-icon-component',
  imports: [CommonModule],
  templateUrl: './icon.component.html',
  styleUrls: ['./icon.component.css']
})
export class IconComponent {
  @Input() iconName!: 'dashboard' | 'finance-path' | 'learning' | 'profile' | 'settings';
  @Input() class: string = 'h-5 w-5';
}