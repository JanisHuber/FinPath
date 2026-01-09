import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { IconComponent } from '../icon-component/icon.component';

@Component({
  selector: 'app-side-bar',
  standalone: true,
  imports: [CommonModule, TranslateModule, IconComponent],
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
})
export class SidebarComponent {
  @Output() linkClick = new EventEmitter<void>();
}
