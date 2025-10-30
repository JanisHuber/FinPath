import { Component, Input, Output, EventEmitter, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-side-bar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
})
export class SidebarComponent {
  @Input() collapsed = false; // true = schmal
  @Output() collapsedChange = new EventEmitter<boolean>();
  @Output() linkClick = new EventEmitter<void>();

  // Keep the initial responsive state: on small screens start collapsed
  ngOnInit(): void {
    if (this.isMobile) {
      this.collapsed = true;
      this.collapsedChange.emit(this.collapsed);
    }
  }

  // Re-evaluate when resizing the window (keeps behaviour coherent)
  @HostListener('window:resize')
  onResize() {
    // If switching to mobile view, ensure sidebar is collapsed by default
    if (this.isMobile && !this.collapsed) {
      // keep it open if user expanded explicitly — do nothing
      // (we don't force-close to avoid surprising the user)
    }
  }

  get isMobile() {
    return typeof window !== 'undefined' && window.innerWidth < 768; // same breakpoint as Tailwind md
  }

  toggleCollapse() {
    this.collapsed = !this.collapsed;
    this.collapsedChange.emit(this.collapsed);
  }

  closeOnOverlay() {
    if (!this.collapsed && this.isMobile) {
      this.collapsed = true;
      this.collapsedChange.emit(this.collapsed);
    }
  }

  get widthClass() {
    return this.collapsed ? 'w-[78px]' : 'w-72'; // ähnlich Screenshot
  }
}
