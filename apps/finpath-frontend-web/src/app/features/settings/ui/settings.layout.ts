import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-settings-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './settings.layout.html',
  styleUrls: ['./settings.layout.css']
})

export class SettingsLayout {}