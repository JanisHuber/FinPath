import { http, HttpResponse } from 'msw';
import userdata from './data/user-data.json';

export const handlers = [
  http.get('/api/v1/user/data', () => HttpResponse.json(userdata)),
  // weitere mocks:
  // http.get('/api/v1/learning/progress', () => HttpResponse.json({...})),
  // http.post('/api/v1/goals', async ({ request }) => {
  //   const body = await request.json();
  //   return HttpResponse.json({ id: 'g2', ...body }, { status: 201 });
  // }),
];
