import React from 'react';
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';

const SpeedtestChart = ({results}) => (

  <ResponsiveContainer height={500}>
    <LineChart data={results}
               margin={{top: 5, right: 20, bottom: 5, left: 0}}>
      <CartesianGrid stroke="#ccc" strokeDasharray="5 5"/>
      <Tooltip/>

      <Line type="monotone"
            dataKey="download"
            stroke="#6afff3"
            activeDot={{r: 6}}/>
      <Line type="monotone"
            dataKey="upload"
            stroke="#bf71ff"
            activeDot={{r: 6}}/>
      <XAxis dataKey="timestamp"/>
      <YAxis orientation="right" unit="Mbps"/>
      <Legend/>
    </LineChart>
  </ResponsiveContainer>
);

export default SpeedtestChart;


