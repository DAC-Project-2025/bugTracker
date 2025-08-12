import React from 'react';
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Legend
} from 'recharts';
import CustomToolTip from './CustomToolTip';
import CustomLegend from './CustomLegend';

const CustomPieChart = ({ data, color }) => {
//   if (!Array.isArray(data) || data.length === 0) return <p>No data available</p>;

  return (
    <ResponsiveContainer width="100%" height={325}>
      <PieChart>
        <Pie
          data={data}
          dataKey="count"
          nameKey="status"
          cx="50%"
          cy="50%"
          outerRadius={130}
          innerRadius={100}
          labelLine={false}
        >
          {data.map((entry, index) => (
            <Cell
              key={`cell-${index}`}
              fill={color[index % color.length]}
            />
          ))}
        </Pie>
        <Tooltip content={<CustomToolTip />} />
        <Legend content={<CustomLegend />}/>
      </PieChart>
    </ResponsiveContainer>
  );
};

export default CustomPieChart;
