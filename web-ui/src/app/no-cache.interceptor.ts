import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CacheControlInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.method === 'GET') {
      const now = new Date().getTime();
      // Check if the request URL already has query parameters
      const separator = req.url.includes('?') ? '&' : '?';
      const newUrl = `${req.url}${separator}timestamp=${now}`;
      const newReq = req.clone({ url: newUrl });
      return next.handle(newReq);
    }
    // For non-GET requests, just forward them without modification
    return next.handle(req);
  }
}
