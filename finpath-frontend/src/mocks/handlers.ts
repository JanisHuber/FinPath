import { http, HttpResponse } from 'msw';
import userdata from './data/user.data.json';
import checkindata from './data/check-in.data.json';
import financialSummaryData from './data/financial-management.data.json';

export const handlers = [
  http.get('/api/v1/user/data', () => HttpResponse.json(userdata)),
  http.get('/api/v1/dashboard/check-in', () => HttpResponse.json(checkindata)),
  http.get('api/v1/dashboard/monthly-summary', () => HttpResponse.json(financialSummaryData)),
  // weitere mocks:
  // http.get('/api/v1/learning/progress', () => HttpResponse.json({...})),
  // http.post('/api/v1/goals', async ({ request }) => {
  //   const body = await request.json();
  //   return HttpResponse.json({ id: 'g2', ...body }, { status: 201 });
  // }),
];
