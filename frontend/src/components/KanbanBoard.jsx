// src/KanbanBoard.js
import React from "react";
import { KanbanComponent } from "@syncfusion/ej2-react-kanban";
import DashbordLayout from "./layout/DashbordLayout";

const KanbanBoard = () => {
  const data = [
    {
      Id: 1,
      Title: "Task 1",
      Status: "Open",
      Description: "Description for Task 1",
    },
    {
      Id: 2,
      Title: "Task 2",
      Status: "In Progress",
      Description: "Description for Task 2",
    },
    {
      Id: 3,
      Title: "Task 3",
      Status: "Done",
      Description: "Description for Task 3",
    },
  ];

//   const kanbanData = {
//     dataSource: data,
//     fields: {
//       id: "Id",
//       title: "Title",
//       content: "Description",
//       status: "Status",
//     },
//   };

const cardSettings = {
    contentField: 'Description',
    headerField: 'Title',
};

const columns = [
    { headerText: 'Open', keyField: 'Open' },
    { headerText: 'In Progress', keyField: 'In Progress' },
    { headerText: 'Done', keyField: 'Done' },
];

  const kanbanSettings = {
    columns: [
      { headerText: "Open", keyField: "Open" },
      { headerText: "In Progress", keyField: "In Progress" },
      { headerText: "Done", keyField: "Done" },
    ],
  };

  return (
    <DashbordLayout  activeMenu="Kanban Board">
    <div className="kanban-container">
      <KanbanComponent
        id="kanban"
        keyField="Status"
        dataSource={data}
        cardSettings={cardSettings}
        columns={columns}
        allowDragAndDrop={true}
      />
    </div>
    </DashbordLayout>
  );
};

export default KanbanBoard;
