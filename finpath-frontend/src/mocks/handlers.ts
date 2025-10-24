import { http, HttpResponse } from 'msw';
import userdata from './data/user.data.json';
import checkindata from './data/check-in.data.json';

export const handlers = [
  http.get('/api/v1/user/data', () => HttpResponse.json(userdata)),
  http.get('/api/v1/dashboard/checkin', () => HttpResponse.json(checkindata)),
  // weitere mocks:
  // http.get('/api/v1/learning/progress', () => HttpResponse.json({...})),
  // http.post('/api/v1/goals', async ({ request }) => {
  //   const body = await request.json();
  //   return HttpResponse.json({ id: 'g2', ...body }, { status: 201 });
  // }),
];
