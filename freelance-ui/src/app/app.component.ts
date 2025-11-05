import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RdfApiService } from './services/rdf-api.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HttpClientModule], 
  providers: [RdfApiService],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'freelance-ui';
}
